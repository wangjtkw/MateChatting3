package com.example.matechatting.tcpprocess

import android.content.Context
import android.util.Log
import com.example.matechatting.tcpprocess.beans.PostCardMessage
import com.example.matechatting.tcpprocess.handlers.ClientChannelInitializer
import com.example.matechatting.tcpprocess.utils.MessageFactory
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

import java.net.InetSocketAddress

class NettyClient(private val host: String, private val port: Int,private val context: Context) {

    fun start(token: String) {
        val group = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group)
                .channel(NioSocketChannel::class.java)
                .remoteAddress(InetSocketAddress(host, port))
                .handler(ClientChannelInitializer(PostCardMessage.Message.getDefaultInstance(),context))
            val future = bootstrap.connect().sync()
            channel = future.channel()
            Log.d("aaa","channel $channel")
            channel!!.writeAndFlush(MessageFactory.loginMessage(token))
            future.channel().closeFuture().sync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        var channel: Channel? = null
    }
}
