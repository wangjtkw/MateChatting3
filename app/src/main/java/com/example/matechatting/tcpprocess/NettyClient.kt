package com.example.matechatting.tcpprocess

import android.content.Context
import android.util.Log
import com.example.matechatting.HOST
import com.example.matechatting.PORT
import com.example.matechatting.tcpprocess.beans.PostCardMessage
import com.example.matechatting.tcpprocess.handlers.ClientChannelInitializer
import com.example.matechatting.tcpprocess.utils.MessageFactory
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.runOnNewThread
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress

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
                    .remoteAddress(InetSocketAddress(HOST, PORT))
                    .handler(
                        ClientChannelInitializer(
                            PostCardMessage.Message.getDefaultInstance(),
                            context
                        )
                    )
                val future = bootstrap!!.connect().sync()
                if (future != null && future.isSuccess) {
                    Log.d(TAG, "start 连接")
                    channel = future.channel()
                    isConnect = true
                    channel!!.writeAndFlush(MessageFactory.loginMessage(token))
                    future.channel().closeFuture().sync()
                } else {
                    Log.d(TAG, "start 错误")
                    isConnect = false
                    reconnect(context)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                reconnect(context)
            }
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
