package com.example.matechatting.mainprocess.milelist

import android.annotation.SuppressLint
import android.util.Log
import com.example.matechatting.bean.UserBean
import com.example.matechatting.database.HasMessageDao
import com.example.matechatting.database.UserInfoDao
import com.example.matechatting.utils.PinyinUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MileListRepository(private val userInfoDao: UserInfoDao, private val hasMessageDao: HasMessageDao) {

    @SuppressLint("CheckResult")
    fun getUserByState(state: Int, callback: (List<UserBean>) -> Unit) {
        userInfoDao.getUserInfoByState(state)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it)
            }, {
                val array = ArrayList<UserBean>()
                callback(array)
            })
    }

    fun getAllNewChatting(callback: (List<UserBean>) -> Unit) {
        hasMessageDao.getHasMessage(true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                getAllNewChattingByIds(it,callback)
            }
            .doOnError {
                val array = ArrayList<UserBean>()
                callback(array)
            }
            .subscribe()
    }

    private fun getAllNewChattingByIds(list: List<Int>, callback: (List<UserBean>) -> Unit) {
        userInfoDao.getAllNewChatting(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(callback)
            .doOnError { }
            .subscribe()
    }

    fun getAllFriendFromDB(callback: (List<UserBean>) -> Unit) {
        userInfoDao.getAllFriend()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("aaa", "数据库查询好友 $it")
                callback(it)
            }, {
                val array = ArrayList<UserBean>()
                callback(array)
            })
    }


    fun updateState(userBean: UserBean, state: Int, callback: () -> Unit = {}) {
        userBean.state = state
        userBean.pinyin = PinyinUtil.getFirstHeadWordChar(userBean.name)
        userInfoDao.insertUserInfo(userBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }

    fun removeFriend(userBean: UserBean, callback: () -> Unit = {}) {
        userInfoDao.deleteUserInfo(userBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }

    companion object {
        @Volatile
        private var instance: MileListRepository? = null

        fun getInstance(userInfoDao: UserInfoDao,hasMessageDao: HasMessageDao) =
            instance ?: synchronized(this) {
                instance ?: MileListRepository(userInfoDao,hasMessageDao).also { instance = it }
            }
    }
}