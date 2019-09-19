package com.example.matechatting.mainprocess.forgetpassword

import com.example.matechatting.base.BaseRepository
import com.example.matechatting.network.CheckResetCodeService
import com.example.matechatting.network.IdeaApi
import com.example.matechatting.network.SendResetMessageService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ForgetPasswordRepository : BaseRepository {
    fun sendMessage(phoneNum: Long,callback:(String) -> Unit) {
        IdeaApi.getApiService(SendResetMessageService::class.java).getResetToken(phoneNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (it.success){
                    callback(it.payload)
                }else{
                    callback("error")
                }
            }
            .subscribe()
    }

    fun checkCode(phoneNum: Long, code: String, callback: (String) -> Unit) {
        IdeaApi.getApiService(CheckResetCodeService::class.java).checkCode(phoneNum, code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (it.success){
                    callback(it.payload)
                }else{
                    callback("error")
                }
            }
            .subscribe()
    }
}