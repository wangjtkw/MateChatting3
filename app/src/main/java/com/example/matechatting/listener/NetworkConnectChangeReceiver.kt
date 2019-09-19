package com.example.matechatting.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class NetworkConnectChangeReceiver : BroadcastReceiver() {

    private val TAG = "NetworkChangeReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        if (intent.action === ConnectivityManager.CONNECTIVITY_ACTION) {
//            when (isNetworkConnected(context)) {
//                NetworkState.MOBILE -> setToast("移动网络已连接", context)
//                NetworkState.WIFI -> setToast("WIFI网络已连接", context)
//                else -> setToast("网络连接已断开", context)
//            }
        }
    }

}
