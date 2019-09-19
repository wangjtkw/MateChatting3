package com.example.matechatting.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.matechatting.R

class ToastUtilWarning {
    private lateinit var inflater: View
    private lateinit var textView: TextView
    private lateinit var layout:ConstraintLayout

    fun setToast(context: Context,message: String) {
        init(message,context)
        val toast = Toast(context)
        toast.setGravity(Gravity.TOP ,0,0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = inflater
        toast.show()
    }

    private fun init(message: String,context: Context){
        inflater = LayoutInflater.from(context).inflate(R.layout.temp_toast_warning,null)
        textView = inflater.findViewById(R.id.toast_text)
        textView.text = message
        layout = inflater.findViewById(R.id.toast_layout)

    }
}



