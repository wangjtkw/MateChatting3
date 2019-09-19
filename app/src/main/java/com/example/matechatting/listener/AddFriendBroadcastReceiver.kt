package com.example.matechatting.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.matechatting.utils.ToastUtilWarning

class AddFriendBroadcastReceiver : BroadcastReceiver() {

    /**
     * subject:0(成功)，1（已经发送过请求），2（已经是好友），3（对方正请求你为好友）
     */
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.getIntExtra("subject", -1)) {
            1 -> {
                ToastUtilWarning().setToast(context, "您已发送过请求")
            }
            2 -> {
                ToastUtilWarning().setToast(context, "你们已经是好友了")
            }
            3 -> {
                ToastUtilWarning().setToast(context, "对方正请求添加您为好友，请到通讯录查看")
            }
        }
    }
}
