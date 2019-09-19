package com.example.matechatting.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.matechatting.MyApplication
import com.example.matechatting.tcpprocess.service.NetService
import com.example.matechatting.utils.ToastUtilWarning

class NetChangeReceiverReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        ToastUtilWarning().setToast(MyApplication.getContext(),"运行")
            val tIntent = Intent(context, NetService::class.java)
            context.startService(tIntent)
    }
}
