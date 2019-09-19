package com.example.matechatting.mainprocess.infodetail

import com.example.matechatting.MyApplication
import com.example.matechatting.base.BaseRepository
import com.example.matechatting.bean.UserBean
import com.example.matechatting.database.UserInfoDao
import com.example.matechatting.network.GetUserByIdService
import com.example.matechatting.network.IdeaApi
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.PinyinUtil
import com.example.matechatting.utils.isNetworkConnected
import com.example.matechatting.utils.runOnNewThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InfoDetailRepository(private val userInfoDao: UserInfoDao) : BaseRepository {

    fun getDetail(id: Int, callback: (UserBean) -> Unit) {
        if (isNetworkConnected(MyApplication.getContext()) == NetworkState.NONE) {
            getDetailFromDB(id, callback)
        } else {
            getDetailFromNet(id, callback)
        }
    }

    private fun getDetailFromDB(id: Int, callback: (UserBean) -> Unit) {
        userInfoDao.getUserInfo(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it)
            }, {})
    }

    private fun getDetailFromNet(id: Int, callback: (UserBean) -> Unit) {
        IdeaApi.getApiService(GetUserByIdService::class.java, false).getUser(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                val userInfo = setInfo(it)
                callback(userInfo)
                saveInDB(userInfo)
            }
            .subscribe()
    }

    private fun saveInDB(userBean: UserBean) {
        runOnNewThread {
            userBean.pinyin = PinyinUtil.getFirstHeadWordChar(userBean.name)
            userInfoDao.insertUserInfo(userBean)
        }
    }

    private fun setInfo(userBean: UserBean): UserBean {
        userBean.apply {
            if (!directions.isNullOrEmpty()) {
                val sb = java.lang.StringBuilder()
                for (s:String in directions!!){
                    sb.append(" ")
                    sb.append(s)
                }
                direction = sb.toString()
                direction = sb.toString()
            }
            val sb = StringBuilder()
            sb.append(graduationYear)
            sb.append("年入学")
            graduation = sb.toString()
        }
        return userBean
    }

    companion object {
        @Volatile
        private var instance: InfoDetailRepository? = null

        fun getInstance(userInfoDao: UserInfoDao) =
            instance ?: synchronized(this) {
                instance ?: InfoDetailRepository(userInfoDao).also { instance = it }
            }
    }

}
