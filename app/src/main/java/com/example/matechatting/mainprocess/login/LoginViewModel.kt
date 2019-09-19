package com.example.matechatting.mainprocess.login

import androidx.lifecycle.ViewModel
import com.example.matechatting.MyApplication
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.isNetworkConnected

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    fun getUserInfo(callback: () -> Unit){
        repository.getMineFromNet(callback)
    }

    fun checkAccount(account: String?, password: String?, callback: (state: Int,List<String>) -> Unit) {
        if (account.isNullOrEmpty()) {
            callback(LoginState.ACCOUNT_NULL, arrayListOf(""))
        }
        if (password.isNullOrEmpty()) {
            callback(LoginState.PASSWORD_NULL,arrayListOf(""))
        }
        val state = isNetworkConnected(MyApplication.getContext())
        if (state == NetworkState.NONE) {
            repository.checkFromDatabase(account!!, password!!, callback)
        } else {
            repository.checkFromNetwork(account!!, password!!, callback)
        }
    }
}

