package com.example.matechatting.tcpprocess.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import com.example.matechatting.*
import com.example.matechatting.tcpprocess.NettyClient
import com.example.matechatting.tcpprocess.repository.TCPRepository
import com.example.matechatting.tcpprocess.utils.MessageFactory
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.runOnNewThread

class NetService : Service() {
    private val remoteAcceptCallbackList = RemoteCallbackList<AcceptFriendListener>()

    override fun onCreate() {
        super.onCreate()
        InjectorUtils.getAccountRepository(this).getToken {
            if (it.isNotEmpty()) {
                runOnNewThread {
                    NettyClient(HOST, PORT, this).start(it)
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        netBinder = NetBinder()
        return netBinder!!
    }

    override fun onDestroy() {
        super.onDestroy()
        if (netBinder != null) {
            netBinder = null
        }
    }

    private fun addFriendService(friendId: Int, uuid: String) {
        val message = MessageFactory.addFriendRequest(friendId, uuid)
        NettyClient.channel?.writeAndFlush(message)
        Log.d("aaa", "addFriendService")
    }

    private fun acceptFriendService(friendId: Int) {
        TCPRepository.changeUserState(4, friendId) {
            TCPRepository.getLoginStateById(friendId) {
                TCPRepository.updateOnLineState(it, friendId) {
                    val message = MessageFactory.acceptFriendResponse(friendId)
                    NettyClient.channel?.writeAndFlush(message)
                    NettyClient.channel?.writeAndFlush(MessageFactory.stringMessage("我们已经成为好友啦！", friendId))
                    val intent = Intent(ACCEPT_FRIEND_ACTION)
                    intent.putExtra("subject", 3)
                    this.sendBroadcast(intent)
                }
            }
        }
    }

    private fun sendMessage(message: String?, otherId: Int) {
        NettyClient.channel?.writeAndFlush(MessageFactory.stringMessage(message!!, otherId))
    }

    inner class NetBinder : TCPInterface.Stub() {
        override fun sendMsg(message: String?, otherId: Int) {
            sendMessage(message, otherId)
        }

        override fun acceptFriend(friendId: Int) {
            acceptFriendService(friendId)
        }

        override fun registerAcceptCallback(callback: AcceptFriendListener?) {
            remoteAcceptCallbackList.register(callback)
        }

        override fun unRegisterAcceptCallback(callBack: AcceptFriendListener?) {
            remoteAcceptCallbackList.unregister(callBack)
        }

        override fun addFriend(friendId: Int, uuid: String) {
            addFriendService(friendId, uuid)
        }
    }

    companion object {
        var netBinder: NetBinder? = null
    }

}
