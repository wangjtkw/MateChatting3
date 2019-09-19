package com.example.matechatting.utils.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.matechatting.R

class OKDialogUtil {
    private lateinit var dialog: Dialog
    private lateinit var view: View
    private lateinit var okTextView: TextView
    private lateinit var okButton: TextView

    fun initOKDialog(context: Context,msg:String, callback: () -> Unit) {
        dialog = Dialog(context, R.style.AccessPermissionDialog)
        view = LayoutInflater.from(context).inflate(R.layout.temp_ok_dialog, null)
        okTextView = view.findViewById(R.id.ok_dialog_text_view)
        okButton = view.findViewById(R.id.ok_dialog_button)
        initButton(callback)
        okTextView.text = msg
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
        dialog.show()
    }

    private fun initButton(callback: () -> Unit) {
        okButton.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }
}