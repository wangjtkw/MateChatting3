package com.example.matechatting.mainprocess.chatting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.database.ChattingDao
import com.example.matechatting.database.UserInfoDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChattingRepository(private val chattingDao: ChattingDao, private val userInfoDao: UserInfoDao) {
    fun getChattingFromDBPaging(otherId: Int, userId: Int, callback: (LiveData<PagedList<ChattingBean>>) -> Unit) {
        val detailList = LivePagedListBuilder(
            chattingDao.getChattingBeanByIdPaging(otherId, userId), PagedList
                .Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(ENABLE_PLACEHOLDER).build()
        ).build()
        Log.d("aaa", "detailList" + detailList)
        callback(detailList)
    }

    fun getChatting(otherId: Int, userId: Int, callback: (List<ChattingBean>) -> Unit) {
        chattingDao.getChattingBeanById(otherId, userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it)
            }, {})
    }

    fun insertMessage(chattingBean: ChattingBean, callback: () -> Unit) {
        chattingDao.insertChattingBean(chattingBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }

    fun changeState(state: Int, otherId: Int, callback: () -> Unit) {
        userInfoDao.updateStateById(state, otherId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }

    companion object {
        private const val PAGE_SIZE = 1
        private const val ENABLE_PLACEHOLDER = true

        @Volatile
        private var instance: ChattingRepository? = null

        fun getInstance(chattingDao: ChattingDao,userInfoDao: UserInfoDao) =
            instance ?: synchronized(this) {
                instance
                    ?: ChattingRepository(chattingDao,userInfoDao).also { instance = it }
            }
    }
}