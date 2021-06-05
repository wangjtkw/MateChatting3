package com.example.matechatting.tcpprocess.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.matechatting.ACCEPT_FRIEND_ACTION
import com.example.matechatting.AcceptFriendListener
import com.example.matechatting.TCPInterface
import com.example.matechatting.tcpprocess.NettyClient
import com.example.matechatting.tcpprocess.repository.TCPRepository
import com.example.matechatting.tcpprocess.utils.MessageFactory
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.isNetworkConnected
import com.example.matechatting.utils.runOnNewThread
import io.reactivex.Flowable
import org.intellij.lang.annotations.Flow

class NetService : Service() {
    private var networkReceiver: NetworkReceiver? = null
    private var myNetworkCallback: MyNetworkCallback? = null
    private val remoteAcceptCallbackList = RemoteCallbackList<AcceptFriendListener>()

    private val TAG = "NetService"

    override fun onCreate() {
        super.onCreate()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            myNetworkCallback = MyNetworkCallback()
//            val networkRequest = NetworkRequest.Builder().build()
//            val connectManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            connectManager.registerNetworkCallback(networkRequest, myNetworkCallback!!)
//        } else {
//            networkReceiver = NetworkReceiver()
//            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//            LocalBroadcastManager.getInstance(this).registerReceiver(networkReceiver!!, filter)
//        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"service 调用")
        connect()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun connect() {
        Log.d(TAG,"connect 调用")
        NettyClient.getInstance().connect(this)
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
        if (networkReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(networkReceiver!!)
            networkReceiver = null
        }
        if (myNetworkCallback != null) {
            val connectManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectManager.unregisterNetworkCallback(myNetworkCallback!!)
            myNetworkCallback = null
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

    inner class NetworkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (isNetworkConnected(context) != NetworkState.NONE && !NettyClient.getInstance().isConnect) {
                connect()
                Log.e(TAG, "connecting ...")
            }
        }
    }

    inner class MyNetworkCallback : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if (isNetworkConnected(this@NetService) != NetworkState.NONE && !NettyClient.getInstance().isConnect) {
                connect()
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d(TAG, "网络已断开")
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d(TAG, "网络已连接")
        }
    }

    companion object {
        var netBinder: NetBinder? = null
    }

}
