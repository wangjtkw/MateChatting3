package com.example.matechatting.tcpprocess

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.matechatting.*
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.bean.UserBean
import com.example.matechatting.tcpprocess.beans.Constant.*
import com.example.matechatting.tcpprocess.beans.PostCardMessage
import com.example.matechatting.tcpprocess.repository.TCPRepository
import com.example.matechatting.tcpprocess.repository.TCPRepository.changeHasMessage
import com.example.matechatting.tcpprocess.repository.TCPRepository.getAllFriendFromDB
import com.example.matechatting.tcpprocess.repository.TCPRepository.getAllFriendFromNet
import com.example.matechatting.tcpprocess.repository.TCPRepository.getAllIdFromDB
import com.example.matechatting.tcpprocess.repository.TCPRepository.updateOnLineState
import com.example.matechatting.tcpprocess.repository.TCPRepository.updateOnLineStateList
import com.example.matechatting.tcpprocess.repository.TCPRepository.updateState
import com.example.matechatting.tcpprocess.utils.MessageFactory
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.sql.Timestamp

object ReadMessage {
    private val TAG = ReadMessage::class.java.name
    fun readMessage(message: PostCardMessage.Message, context: Context) {
        Log.d(TAG, "message ${message.subject}")
        when (message.subject) {
            //更新数据集合
            //当前在线好友集合
            FRIENDS_LIST -> {
                friendsList(message, context)
            }
            //别人请求添加好友
            ADD_FRIEND_REQUEST -> {
                Log.d(TAG, "加好友请求")
                addFriendRequest(message, context)
            }
            //收到别人发的消息
            STRING_MESSAGE -> {
                stringMessage(message, context)
            }
            //发送的消息已成功接收
            SUCCESS -> {
                success(message, context)
            }
            //好友上线
            LOG_IN -> {
                logIn(message, context)
            }
            //好友下线
            LOG_OUT -> {
                logOut(message, context)
            }
            ERROR -> {
                error(message, context)
            }
            //好友同意
            ACCEPT_FRIEND_RESPONSE -> {
                acceptFriendResponse(message, context)
            }
        }
    }

    private fun error(message: PostCardMessage.Message, context: Context) {
        val error = message.payload.toStringUtf8()
        Log.d(TAG, "error $error")
        when (error) {
            //Toast系列
            //加好友请求已发送过
            ADD_REQUEST_HAS_BEEN_SEND -> {
                addRequestHasBeenSend(message, context)
            }
            //和对方已经是好友，不用再次添加
            HAVE_BEEN_FRIENDS_WITH_ACCEPT_USER -> {
                hasBeenFriendWithAcceptUser(message, context)
            }
            CANT_ADD_YOUR_SELF -> {
                Log.d("aaa", "不能加自己为好友")
                canNotAddYourself(message, context)
            }
            //对方已经向你发起请求
            ACCEPT_USER_HAS_SEND_ADD_REQUEST -> {
                acceptUserHasSendAddRequest(message, context)
            }
        }
    }

    /**
     * subject:0(成功)，1（已经发送过请求），2（已经是好友），3（对方正请求你为好友）,4（不能加自己为好友）
     */
    private fun addRequestHasBeenSend(message: PostCardMessage.Message, context: Context) {
        val intent = Intent(ADD_FRIEND_REQUEST_BROADCAST_ACTION)
        intent.putExtra("subject", 1)
        context.sendBroadcast(intent)
    }

    private fun hasBeenFriendWithAcceptUser(message: PostCardMessage.Message, context: Context) {
        val intent = Intent(ADD_FRIEND_REQUEST_BROADCAST_ACTION)
        intent.putExtra("subject", 2)
        context.sendBroadcast(intent)
    }

    private fun acceptUserHasSendAddRequest(message: PostCardMessage.Message, context: Context) {
        val intent = Intent(ADD_FRIEND_REQUEST_BROADCAST_ACTION)
        intent.putExtra("subject", 3)
        context.sendBroadcast(intent)
    }

    private fun canNotAddYourself(message: PostCardMessage.Message, context: Context) {
        val intent = Intent(ADD_FRIEND_REQUEST_BROADCAST_ACTION)
        intent.putExtra("subject", 4)
        context.sendBroadcast(intent)
    }

    private fun friendsList(message: PostCardMessage.Message, context: Context) {
        val json = message.payload.toStringUtf8()
        val jsonParser = JsonParser()
        val jsonArray = jsonParser.parse(json).asJsonArray
        val gson = Gson()
        val array = ArrayList<Int>()
        for (element: JsonElement in jsonArray) {
            val a = gson.fromJson(element, Int::class.java)
            array.add(a)
        }
        Log.d("aaa", "在线好友 $array")
        getAllFriendFromDB(context) { db ->
            getAllFriendFromNet {
                for (bean: UserBean in db) {
                    Log.d("aaa", "state ${bean.state}")
                    updateState(bean.state, bean.id)
                }
                updateOnLineStateList(true, array){
                    getAllIdFromDB {
                        val idArray = ArrayList(it)
                        idArray.removeAll(array)
                        updateOnLineStateList(false,idArray){
                            val intent = Intent(ON_LINE_FRIEND)
                            intent.putExtra("subject", HAS_NEW_FRIEND)
                            context.sendBroadcast(intent)
                        }
                    }
                }
            }
        }
    }

    private fun logIn(message: PostCardMessage.Message, context: Context) {
        val id = message.sendUserId
        Log.d("aaa", "登陆 $id")
        updateOnLineState(true, id) {
            val intent = Intent(LOG_IN_ACTION)
            context.sendBroadcast(intent)
        }
    }

    private fun logOut(message: PostCardMessage.Message, context: Context) {
        val id = message.sendUserId
        Log.d("aaa", "登出 $id")
        updateOnLineState(false, id) {
            val intent = Intent(LOG_OUT_ACTION)
            context.sendBroadcast(intent)
        }
    }

    private fun acceptFriendResponse(message: PostCardMessage.Message, context: Context) {
        val id = message.sendUserId
        Log.d("aaa", "好友同意添加 $id")
        TCPRepository.getUserInfo(id) {
            if (it) {
                TCPRepository.changeUserState(3, id) {
                    TCPRepository.getLoginStateById(id) { login ->
                        updateOnLineState(login, id) {
                            Log.d("aaa", "changeUserState ")
                            val intent = Intent(ACCEPT_FRIEND_ACTION)
                            intent.putExtra("subject", HAS_NEW_FRIEND)
                            context.sendBroadcast(intent)
                        }
                    }
                }
            } else {
                TCPRepository.getAndSaveFriendInfo(3, id) {
                    TCPRepository.getLoginStateById(id) { login ->
                        updateOnLineState(login, id) {
                            Log.d("aaa", "getAndSaveFriendInfo ")
                            val intent = Intent(ACCEPT_FRIEND_ACTION)
                            intent.putExtra("subject", HAS_NEW_FRIEND)
                            context.sendBroadcast(intent)
                        }
                    }
                }
            }
        }
    }


    private fun addFriendRequest(message: PostCardMessage.Message, context: Context) {
        val id = message.sendUserId
        Log.d("aaa", "好友同意添加 $message")
        TCPRepository.getAndSaveFriendInfo(2, id) {
            val intent = Intent(ACCEPT_FRIEND_ACTION)
            intent.putExtra("subject", HAS_NEW_FRIEND)
            context.sendBroadcast(intent)
        }
    }

    private fun stringMessage(message: PostCardMessage.Message, context: Context) {
        val uuid = message.id
        var bean: ChattingBean
        message.apply {
            bean = ChattingBean(
                sendUserId,
                acceptUserId,
                payload.toStringUtf8(),//后期可能会更改，需要转成文件类型
                true,
                Timestamp(System.currentTimeMillis()).toString(),
                false
            )
        }
        Log.d(TAG,"stringMessage 调用")
        TCPRepository.saveMessage(bean) {
            changeHasMessage(bean.userId, bean.otherId) {
                NettyClient.channel?.writeAndFlush(MessageFactory.success(uuid))
                val intent = Intent(HAS_NEW_MESSAGE_ACTION)
                intent.putExtra("subject", bean)
                context.sendBroadcast(intent)
            }
        }
    }

    private fun success(message: PostCardMessage.Message, context: Context) {

    }

}