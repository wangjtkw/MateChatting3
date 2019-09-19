package com.example.matechatting.mainprocess.bindphone

import com.example.matechatting.base.BaseRepository
import com.example.matechatting.network.CheckCodeService
import com.example.matechatting.network.IdeaApi
import com.example.matechatting.network.SendMessageService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BindPhoneRepository : BaseRepository {

    fun sendMessage(phoneNum: Long) {
        IdeaApi.getApiService(SendMessageService::class.java).getCheckCode(phoneNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {}
            .subscribe()
    }

    fun checkCode(phoneNum: Long, code: String, callback: (Boolean) -> Unit) {
        IdeaApi.getApiService(CheckCodeService::class.java).checkCode(phoneNum, code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                callback(it.success)
            }
            .subscribe()
    }
}