package com.example.matechatting.tcpprocess.handlers

import android.content.Context
import android.util.Log
import com.example.matechatting.tcpprocess.NettyClient
import com.example.matechatting.tcpprocess.ReadMessage
import com.example.matechatting.tcpprocess.beans.PostCardMessage
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

@ChannelHandler.Sharable
class ClientHandler(private val context: Context) : ChannelInboundHandlerAdapter() {
    init {
        Log.d(TAG,"ClientHandler 初始化")
    }
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        Log.d(TAG,"channelActive 连接")
        NettyClient.getInstance().isConnect = true
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        Log.d(TAG,"channelInactive 断开")
        NettyClient.getInstance().isConnect = false
        NettyClient.getInstance().reconnect(context)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        Log.d(TAG,"channelRead 读消息")
        val message = msg as PostCardMessage.Message
        ReadMessage.readMessage(message, context)
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
    }
    companion object{
        private val TAG = ClientHandler::class.java.name
    }
}
