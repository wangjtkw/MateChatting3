package com.example.matechatting.tcpprocess

import android.content.Context
import android.util.Log
import com.example.matechatting.HOST
import com.example.matechatting.PORT
import com.example.matechatting.tcpprocess.beans.PostCardMessage
import com.example.matechatting.tcpprocess.handlers.ClientChannelInitializer
import com.example.matechatting.tcpprocess.handlers.ClientHandler
import com.example.matechatting.tcpprocess.handlers.LoginAuthReqHandler
import com.example.matechatting.tcpprocess.utils.MessageFactory
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.runOnNewThread
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.util.concurrent.Future
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.TimeUnit

class NettyClient private constructor() {
    //是否已经连接服务器
    var isConnect = false

    //重连次数
    private var reconnectNum = Integer.MAX_VALUE

    //重连间隔时间
    private var reconnectIntervalTime: Long = 5000
    private var bootstrap: Bootstrap? = null
    private var group: NioEventLoopGroup? = null

    fun connect(context: Context) {
        Log.d(TAG, "connect 调用")
        InjectorUtils.getAccountRepository(context).getToken {
            Log.d(TAG, "token $it")
            if (it.isNotEmpty()) {
                start(it, context)
            }
        }

    }

    fun start(token: String, context: Context) {
        Log.d(TAG, "start 调用")
        Log.d(TAG, "Thread.currentThread().id ${Thread.currentThread().id}")
        runOnNewThread {
            group = NioEventLoopGroup()
            try {
                bootstrap = Bootstrap()
                bootstrap!!.group(group)
                    .channel(NioSocketChannel::class.java)
                    .handler(LoggingHandler(LogLevel.INFO))
                    .handler(object : ChannelInitializer<Channel>() {
                        override fun initChannel(ch: Channel) {
                            ch.pipeline().addLast(LoggingHandler(LogLevel.ERROR))
                            //decoder
                            ch.pipeline().addLast(ProtobufVarint32FrameDecoder())
                            ch.pipeline()
                                .addLast(ProtobufDecoder(PostCardMessage.Message.getDefaultInstance()))
                            //encoder
                            ch.pipeline().addLast(ProtobufVarint32LengthFieldPrepender())
                            ch.pipeline().addLast(ProtobufEncoder())
                            ch.pipeline().addLast(LoginAuthReqHandler(token))
                            ch.pipeline().addLast(ClientHandler(context))
                        }
                    })
                connect(bootstrap!!, HOST, PORT, MAX_RETRY)
            } finally {
                group!!.shutdownGracefully()
            }
        }
    }

    private fun connect(bootstrap: Bootstrap, host: String, port: Int, retry: Int) {
        val future1 = bootstrap.connect(host, port).addListener { future: Future<in Void?> ->
            if (future.isSuccess) {
                Log.d(TAG, Date().toString() + ": 连接成功!")
            } else if (retry == 0) {
                Log.d(TAG, "重试次数已用完，放弃连接！")
            } else {
                // 第几次重连
                val order = MAX_RETRY - retry + 1
                // 本次重连的间隔
                val delay = 1 shl order
                Log.d(TAG, Date().toString() + ": 连接失败，第" + order + "次重连")
                bootstrap.group().schedule(
                    { connect(bootstrap, host, port, retry - 1) },
                    delay.toLong(),
                    TimeUnit.SECONDS
                )
            }
        }
        try {
            future1.channel().closeFuture().sync()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        Log.d(TAG, "disconnect 调用")
        group?.shutdownGracefully()
    }

    fun reconnect(context: Context) {
        Log.d(TAG, "reconnect 调用")
        if (reconnectNum > 0 && !isConnect) {
            reconnectNum--
            try {
                Thread.sleep(reconnectIntervalTime)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            disconnect()
            connect(context)
        } else {
            disconnect()
        }
    }


    companion object {
        private const val MAX_RETRY = 5 //最大重连次数

        private var TAG = NettyClient::class.java.name
        var channel: Channel? = null

        @Volatile
        private var instance: NettyClient? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: NettyClient().also { instance = it }
            }
    }
}
