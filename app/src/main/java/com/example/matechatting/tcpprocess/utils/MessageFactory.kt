package com.example.matechatting.tcpprocess.utils

import android.util.Log
import com.example.matechatting.tcpprocess.beans.Constant
import com.example.matechatting.tcpprocess.beans.PostCardMessage
import com.google.protobuf.ByteString
import java.util.*

object MessageFactory {

    fun getMessage(subject: String, payload: ByteString, accept_user_id: Int?, id: String?): PostCardMessage.Message {
        val builder = PostCardMessage.Message.newBuilder()
        builder.setSubject(subject).setPayload(payload).setAcceptUserId(accept_user_id!!).id = id
        return builder.build()
    }

    fun getMessage(subject: String, accept_user_id: Int?, id: String): PostCardMessage.Message {
        val builder = PostCardMessage.Message.newBuilder()
        builder.setSubject(subject).setAcceptUserId(accept_user_id!!).id = id
        return builder.build()
    }

    fun getMessage(subject: String,payload: ByteString): PostCardMessage.Message {
        val builder = PostCardMessage.Message.newBuilder()
        builder.setSubject(subject).setPayload(payload)
        return builder.build()
    }


    fun loginMessage(token: String): PostCardMessage.Message {
        Log.d("aaa","loginMessage")
        return getMessage(Constant.LOG_IN, ByteString.copyFromUtf8(token))
    }

    fun addFriendRequest(accept_user_id: Int?, uuid: String): PostCardMessage.Message {
        return getMessage(Constant.ADD_FRIEND_REQUEST, accept_user_id, uuid)
    }

    fun acceptFriendResponse(accept_user_id: Int?): PostCardMessage.Message {
        return getMessage(Constant.ACCEPT_FRIEND_RESPONSE, accept_user_id, UUID.randomUUID().toString())
    }

    fun stringMessage(str: String, acp: Int?): PostCardMessage.Message {
        return getMessage(Constant.STRING_MESSAGE, ByteString.copyFromUtf8(str), acp, UUID.randomUUID().toString())
    }

    fun success(id: String): PostCardMessage.Message {
        return getMessage(Constant.SUCCESS, 0, id)
    }
}
