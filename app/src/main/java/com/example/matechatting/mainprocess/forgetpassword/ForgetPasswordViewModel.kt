package com.example.matechatting.mainprocess.forgetpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.matechatting.mainprocess.bindphone.BindPhoneState
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.ERROR
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.OK

class ForgetPasswordViewModel(private val repository: ForgetPasswordRepository) : ViewModel() {

    fun checkCode(phoneNum: String, code: String, callback: (Int) -> Unit) {
        if (phoneNum.isEmpty()) {
            callback(BindPhoneState.PHONE_NULL)
            return
        }
        if (code.isEmpty()) {
            callback(BindPhoneState.CODE_NULL)
            return
        }
        val regexPhone = Regex("^[1]([3-9])[0-9]{9}\$")
        val resultPhone = regexPhone.find(phoneNum)
        if (resultPhone == null) {
            callback(BindPhoneState.PHONE_ERROR)
            return
        }
        val regexCode = Regex("\\d{6}")
        val resultCode = regexCode.find(code)
        if (resultCode == null) {
            Log.d("aaa", "正则错误")
            callback(BindPhoneState.CODE_ERROR)
            return
        }
        repository.checkCode(phoneNum.toLong(), code) {
            if (it == "error") {
                callback(ERROR)
            } else {
                ForgetPasswordActivity.token = it
                callback(OK)
            }
        }
    }

    fun sendMessage(phoneNum: String, callback: (Int) -> Unit) {
        val regex = Regex("^[1]([3-9])[0-9]{9}\$")
        val result = regex.find(phoneNum)
        if (result == null) {
            callback(BindPhoneState.PHONE_ERROR)
        } else {
            callback(OK)
            repository.sendMessage(phoneNum.toLong()) {
                if (it != "error") {
                    ForgetPasswordActivity.account = it
                } else {
                    callback(ERROR)
                }
            }
        }

    }

}