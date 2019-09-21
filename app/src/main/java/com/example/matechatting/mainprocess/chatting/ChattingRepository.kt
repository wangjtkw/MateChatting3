package com.example.matechatting.mainprocess.chatting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.database.ChattingDao
import com.example.matechatting.database.HasMessageDao
import com.example.matechatting.database.UserInfoDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChattingRepository(
    private val chattingDao: ChattingDao,
    private val userInfoDao: UserInfoDao,
    private val hasMessageDao: HasMessageDao
) {
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

    fun changeState(userId: Int, otherId: Int, callback: () -> Unit) {
        hasMessageDao.deleteHasMessage(userId, otherId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d("aaa","changeState 删除成功")
                callback()
            }
            .doOnError {
                Log.d("aaa","changeState 删除错误")
            }
            .subscribe()
    }

    companion object {
        private const val PAGE_SIZE = 1
        private const val ENABLE_PLACEHOLDER = true

        @Volatile
        private var instance: ChattingRepository? = null

        fun getInstance(chattingDao: ChattingDao, userInfoDao: UserInfoDao, hasMessageDao: HasMessageDao) =
            instance ?: synchronized(this) {
                instance
                    ?: ChattingRepository(chattingDao, userInfoDao, hasMessageDao).also { instance = it }
            }
    }
}