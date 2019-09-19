package com.example.matechatting.tcpprocess.handlers

import android.content.Context
import com.google.protobuf.MessageLite
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler


class ClientChannelInitializer(private val lite: MessageLite, private val context: Context) :
    ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(socketChannel: SocketChannel) {
        socketChannel.pipeline()
            .addLast(LoggingHandler (LogLevel.ERROR))
            //decoder
            .addLast(ProtobufVarint32FrameDecoder())
            .addLast(ProtobufDecoder(lite))
            //encoder
            .addLast(ProtobufVarint32LengthFieldPrepender())
            .addLast(ProtobufEncoder())
            .addLast(ClientHandler(context))
    }
}
