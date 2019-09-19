package com.example.matechatting.utils.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.matechatting.R

class MessageTitleOkDialogUtil {
    private lateinit var dialog: Dialog
    private lateinit var view: View
    private lateinit var messageTitleOkTitle: TextView
    private lateinit var messageTitleOkMessage: TextView
    private lateinit var messageTitleOkButton: Button

    fun initMessageTitleOkDialog(
        context: Context,
        okCallback: () -> Unit
    ): MessageTitleOkDialogUtil {
        dialog = Dialog(context, R.style.AccessPermissionDialog)
        view = LayoutInflater.from(context).inflate(R.layout.temp_title_message_ok_dialog, null)
        messageTitleOkTitle = view.findViewById(R.id.message_ok_title)
        messageTitleOkMessage = view.findViewById(R.id.message_ok_message)
        messageTitleOkButton = view.findViewById(R.id.message_ok_cancel)
        dialog.setContentView(view)
        val dialogWindow = dialog.window
        val windowManager = dialogWindow?.windowManager
        val display = windowManager?.defaultDisplay
        val layoutParams = dialogWindow?.attributes
        val point = Point()
        display?.getSize(point)
        layoutParams?.width = point.x
        dialogWindow?.setGravity(Gravity.CENTER)
        dialogWindow?.attributes = layoutParams

        initOkCallback(okCallback)
        return this
    }

    fun setTitle(title: String): MessageTitleOkDialogUtil {
        messageTitleOkTitle.text = title
        return this
    }

    fun setMessage(message: String): MessageTitleOkDialogUtil {
        messageTitleOkMessage.text = message
        return this
    }

    fun setOver(text: String): MessageTitleOkDialogUtil {
        messageTitleOkButton.text = text
        return this
    }

    fun show() {
        if (!isShowing) {
            isShowing = true
            dialog.show()
        }
    }

    private fun initOkCallback(okCallback: () -> Unit) {
        messageTitleOkButton.setOnClickListener {
            okCallback()
            dialog.dismiss()
            isShowing = false
        }
    }

    companion object {
        private var isShowing = false
    }
}