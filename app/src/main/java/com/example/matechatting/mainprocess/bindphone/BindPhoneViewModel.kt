package com.example.matechatting.mainprocess.bindphone

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.CODE_ERROR
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.CODE_NULL
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.OK
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.PHONE_ERROR
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.PHONE_NULL

class BindPhoneViewModel(private val repository: BindPhoneRepository) : ViewModel() {

    fun sendMessage(phoneNum: String, callback: (Int) -> Unit) {
        val regex = Regex("^[1]([3-9])[0-9]{9}\$")
        val result = regex.find(phoneNum)
        if (result == null) {
            callback(PHONE_ERROR)
        } else {
            callback(OK)
            repository.sendMessage(phoneNum.toLong())
        }

    }

    fun checkCode(phoneNum: String, code: String, callback: (Int) -> Unit) {
        if (phoneNum.isEmpty()){
            callback(PHONE_NULL)
            return
        }
        if (code.isEmpty()){
            callback(CODE_NULL)
            return
        }
        val regexPhone = Regex("^[1]([3-9])[0-9]{9}\$")
        val resultPhone = regexPhone.find(phoneNum)
        if (resultPhone == null) {
            callback(PHONE_ERROR)
            return
        }
        val regexCode = Regex("\\d{6}")
        val resultCode = regexCode.find(code)
        if (resultCode == null) {
            Log.d("aaa","正则错误")
            callback(CODE_ERROR)
            return
        }
        repository.checkCode(phoneNum.toLong(), code) {
            if (it) {
                callback(OK)
            } else {
                callback(CODE_ERROR)
            }
        }
    }
}

class BindPhoneState {
    companion object {
        //成功
        const val OK = 0x000
        //手机号错误
        const val PHONE_ERROR = 0X001
        //验证码错误
        const val CODE_ERROR = 0x002
        //手机号为空
        const val PHONE_NULL = 0x003
        //验证码为空
        const val CODE_NULL = 0x004
        //返回错误
        const val ERROR = 0x005
    }
}