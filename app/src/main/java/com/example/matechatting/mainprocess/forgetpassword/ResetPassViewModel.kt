package com.example.matechatting.mainprocess.forgetpassword

import androidx.lifecycle.ViewModel
import com.example.matechatting.MyApplication
import com.example.matechatting.mainprocess.changepassword.ChangePasswordState
import com.example.matechatting.network.OtherTokenInterceptor
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.isNetworkConnected

class ResetPassViewModel(private val repository: ResetPassRepository) :ViewModel(){

    fun checkNewPassword(new: String, callback: (state: Int) -> Unit) {
        if (new.length < 6) {
            callback(ChangePasswordState.NEW_NO6)
        } else if (new.length > 16) {
            callback(ChangePasswordState.NEW_TOO_LONG)
        } else {
            val result = ChangePasswordState.regex.find(new)
            if (result == null) {
                callback(ChangePasswordState.NEW_ERROR)
            } else {
                callback(ChangePasswordState.NEW_OK)
            }
        }
    }

    fun checkChangePassword(new: String, again: String, callback: (state: Int) -> Unit) {
        val state = isNetworkConnected(MyApplication.getContext())
        if (state == NetworkState.NONE) {
            callback(ChangePasswordState.NO_NET)
            return
        }
        if (new.isEmpty()) {
            callback(ChangePasswordState.NEW_NULL)
            return
        }
        if (again.isEmpty()) {
            callback(ChangePasswordState.AGAIN_NULL)
            return
        }
        checkNewPassword(new,callback)
        val result = ChangePasswordState.regex.find(new)
        if (result == null) {
            callback(ChangePasswordState.NEW_ERROR)
            return
        }
        if (new != again) {
            callback(ChangePasswordState.AGAIN_ERROR)
            return
        }
        /**
         * 验证旧密码是否正确
         */
        changePassword(new,callback)
    }

    private fun changePassword(newPass: String,callback: (state: Int) -> Unit) {
        val token = ForgetPasswordActivity.token
        val interceptor = OtherTokenInterceptor(token)
        repository.changePass(newPass,interceptor){
            if (it){
                callback(ChangePasswordState.OK)
            }else{
                callback(ChangePasswordState.ERROR)
            }
        }
    }


}