package com.example.matechatting.utils.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.matechatting.R

class AccessPermissionDialogUtil {
    lateinit var dialog: Dialog
    private lateinit var view: View
    private lateinit var accessPermissionTitle: TextView
    private lateinit var accessPermissionMessage: TextView
    private lateinit var accessPermissionCancel: TextView
    private lateinit var accessPermissionJump: TextView


    /**
     * 1
     */
    fun initAccessPermissionDialog(
        context: Context,
        jumpCallback: () -> Unit,
        cancelCallback: () -> Unit = {}
    ): AccessPermissionDialogUtil {
        dialog = Dialog(context, R.style.AccessPermissionDialog)
        view = LayoutInflater.from(context).inflate(R.layout.temp_access_permission_dialog, null)
        accessPermissionTitle = view.findViewById(R.id.message_ok_title)
        accessPermissionMessage = view.findViewById(R.id.message_ok_message)
        accessPermissionCancel = view.findViewById(R.id.message_ok_cancel)
        accessPermissionJump = view.findViewById(R.id.access_jump)
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
        Log.d("aaa", "isShowing" + isShowing.toString())

        initCancel(cancelCallback)
        initJump(jumpCallback)
        return this
    }

    /**
     * 2
     */
    fun setTitle(title: String): AccessPermissionDialogUtil {
        accessPermissionTitle.text = title
        return this
    }

    /**
     * 3
     */
    fun setMessage(message: String): AccessPermissionDialogUtil {
        accessPermissionMessage.text = message
        return this
    }

    /**
     * 4
     */
    fun setOver(text: String): AccessPermissionDialogUtil {
        accessPermissionJump.text = text
        return this
    }

    fun setCancel(text: String): AccessPermissionDialogUtil {
        accessPermissionCancel.text = text
        return this
    }

    fun show(){
        if (!isShowing) {
            isShowing = true
            dialog.show()
        }
    }

    private fun initCancel(callback: () -> Unit) {
        accessPermissionCancel.setOnClickListener {
            callback()
            dialog.dismiss()
            isShowing = false
        }
    }


    private fun initJump(callback: () -> Unit) {
        accessPermissionJump.setOnClickListener {
            callback()
            dialog.dismiss()
            isShowing = false
        }
    }

    companion object {
        private var isShowing = false
    }
}