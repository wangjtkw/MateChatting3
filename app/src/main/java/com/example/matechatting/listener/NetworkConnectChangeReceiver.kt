package com.example.matechatting.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.example.matechatting.tcpprocess.service.NetService
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.ToastUtil
import com.example.matechatting.utils.isNetworkConnected

class NetworkConnectChangeReceiver : BroadcastReceiver() {

    private val TAG = "NetworkChangeReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
//        Toast.makeText(context, "网络变化", Toast.LENGTH_SHORT).show()
//        if (isNetworkConnected(context) != NetworkState.NONE) {
//            val serviceIntent = Intent(context, NetService::class.java)
//            context.startService(serviceIntent)
//        }
    }

}
