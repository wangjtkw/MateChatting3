package com.example.matechatting.mainprocess.milelistsearch

import com.example.matechatting.bean.UserBean
import com.example.matechatting.database.UserInfoDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MileListSearchRepository(private val userInfoDao: UserInfoDao) {

    fun searchByKey(key: String, callback: (List<UserBean>) -> Unit) {
        userInfoDao.selectUserByKey(key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it)
            }, {
                val array = ArrayList<UserBean>()
                callback(array)
            })
    }


    companion object {
        @Volatile
        private var instance: MileListSearchRepository? = null

        fun getInstance(userInfoDao: UserInfoDao) =
            instance ?: synchronized(this) {
                instance
                    ?: MileListSearchRepository(userInfoDao).also { instance = it }
            }
    }
}
