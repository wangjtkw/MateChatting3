package com.example.matechatting.utils.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.matechatting.R

class ChooseHeadImageDialogUtil {
    private lateinit var chooseHeadImageDialog: Dialog
    private lateinit var chooseHeadInflater: View
    private lateinit var chooseHeadFromAlbum: TextView
    private lateinit var chooseHeadCancel: TextView

    fun initChooseHeadImageDialog(context: Context,callback: () -> Unit) {
        chooseHeadImageDialog = Dialog(context, R.style.ChooseHeadImageDialog)
        chooseHeadInflater = LayoutInflater.from(context).inflate(R.layout.temp_choose_head_image_dialog, null)
        chooseHeadFromAlbum = chooseHeadInflater.findViewById(R.id.choose_head_from_album)
        chooseHeadCancel = chooseHeadInflater.findViewById(R.id.choose_head_cancel)
        chooseHeadImageDialog.setContentView(chooseHeadInflater)
        val dialogWindow = chooseHeadImageDialog.window
        val windowManager = dialogWindow?.windowManager
        val display = windowManager?.defaultDisplay
        val layoutParams = dialogWindow?.attributes
        val point = Point()
        display?.getSize(point)
        layoutParams?.width = point.x
        dialogWindow?.setGravity(Gravity.BOTTOM)
        dialogWindow?.attributes = layoutParams
        chooseHeadImageDialog.show()
        initChooseHeadFromAlbum(callback)
        initChooseHeadCancel()
    }

    private fun initChooseHeadFromAlbum(callback: () -> Unit) {
        chooseHeadFromAlbum.setOnClickListener {
            callback()
            chooseHeadImageDialog.dismiss()
        }
    }

    private fun initChooseHeadCancel() {
        chooseHeadCancel.setOnClickListener {
            chooseHeadImageDialog.dismiss()
        }

    }
}