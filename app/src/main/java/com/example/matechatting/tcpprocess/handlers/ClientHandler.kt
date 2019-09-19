package com.example.matechatting.tcpprocess.handlers

import android.content.Context
import com.example.matechatting.tcpprocess.ReadMessage
import com.example.matechatting.tcpprocess.beans.PostCardMessage
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

@ChannelHandler.Sharable
class ClientHandler(private val context: Context) : ChannelInboundHandlerAdapter() {
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {

    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val message = msg as PostCardMessage.Message
        ReadMessage.readMessage(message,context)
//        println("---------------------------------------------------------")
//        val message = msg as PostCardMessage.Message
//        println("send_user_id:" + message.sendUserId)
//        println("subject:" + message.subject)
//        println("payload_to_string:" + message.payload.toStringUtf8())
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
    }
}
