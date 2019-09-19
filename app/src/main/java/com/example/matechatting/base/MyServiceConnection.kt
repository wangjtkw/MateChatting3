package com.example.matechatting.base

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.example.matechatting.TCPInterface
import com.example.matechatting.mainprocess.main.MainActivity

class MyServiceConnection : ServiceConnection {
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        MainActivity.service = TCPInterface.Stub.asInterface(p1)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {}

    companion object {
        var isMainBind = false
        var isSearchBind = false

        @Volatile
        private var instance: MyServiceConnection? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: MyServiceConnection().also { instance = it }
            }
    }
}