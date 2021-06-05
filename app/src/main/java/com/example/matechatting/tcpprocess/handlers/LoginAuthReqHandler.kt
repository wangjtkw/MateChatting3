package com.example.matechatting.tcpprocess.handlers

import android.util.Log
import com.example.matechatting.mainprocess.main.MainActivity
import com.example.matechatting.tcpprocess.NettyClient
import com.example.matechatting.tcpprocess.beans.PostCardMessage
import com.example.matechatting.tcpprocess.utils.MessageFactory
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class LoginAuthReqHandler(private val token: String) : ChannelInboundHandlerAdapter() {
    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val message = msg as PostCardMessage.Message
        if (message == null) {
            super.channelRead(ctx, msg)
        }
        NettyClient.channel = ctx.channel()
        ctx.pipeline().remove(this)
        ctx.fireChannelActive()
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        val message = MessageFactory.loginMessage(token)
        ctx.writeAndFlush(message)
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }


}