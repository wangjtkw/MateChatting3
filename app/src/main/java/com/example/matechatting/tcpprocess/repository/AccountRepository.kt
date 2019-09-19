package com.example.matechatting.tcpprocess.repository

import com.example.matechatting.database.AccountDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AccountRepository(private val dao: AccountDao) {

    fun getToken(callback: (String) -> Unit) {
        dao.getLoginToken(true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                callback(it.token)
            }
            .doOnError {
                callback("")
            }
            .subscribe()
    }

    companion object{
        @Volatile
        private var instance: AccountRepository? = null

        fun getInstance(accountDao: AccountDao) =
            instance ?: synchronized(this) {
                instance ?: AccountRepository(accountDao).also { instance = it }
            }
    }
}