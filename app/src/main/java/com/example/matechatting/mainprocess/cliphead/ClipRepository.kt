package com.example.matechatting.mainprocess.cliphead

import android.util.Log
import com.example.matechatting.MyApplication
import com.example.matechatting.base.BaseRepository
import com.example.matechatting.database.UserInfoDao
import com.example.matechatting.network.IdeaApi
import com.example.matechatting.network.PostImageService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class ClipRepository(private val userInfoDao: UserInfoDao) : BaseRepository {

    fun postImage(file: MultipartBody.Part) {
        IdeaApi.getApiService(PostImageService::class.java).postImage(file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {}
            .subscribe()
    }

    fun saveInDB(filePath: String, callback: () -> Unit) {
        Log.d("aaa","Clip user id ${MyApplication.getUserId()}")
        userInfoDao.updateHeadImage(filePath, MyApplication.getUserId()!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("aaa","擦入的行数 $it  文件路径 $filePath")
                userInfoDao.getUserInfo(MyApplication.getUserId()!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.d("aaa","结果 $it")
                        callback()
                    },{})

            }, {
                Log.d("aaa","擦人错误")
                it.printStackTrace()
            })


    }

    companion object {
        @Volatile
        private var instance: ClipRepository? = null

        fun getInstance(userInfoDao: UserInfoDao) =
            instance ?: synchronized(this) {
                instance ?: ClipRepository(userInfoDao).also { instance = it }
            }
    }
}