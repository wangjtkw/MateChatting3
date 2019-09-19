package com.example.matechatting.mainprocess.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.matechatting.MyApplication
import com.example.matechatting.PAGE
import com.example.matechatting.base.BaseRepository
import com.example.matechatting.bean.HomeItemBean
import com.example.matechatting.database.AppDatabase
import com.example.matechatting.database.HomeItemDao
import com.example.matechatting.network.GetHomeItemPageService
import com.example.matechatting.network.GetHomeItemService
import com.example.matechatting.network.IdeaApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class HomeItemRepository(private val homeItemDao: HomeItemDao) : BaseRepository {
    private var first = 0
    private var isFirst = true

    fun getHomeFromDB(callback: (List<HomeItemBean>) -> Unit) {
        homeItemDao.getHomeItemLimit()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //                first += it.size
                Log.d("aaa", "first" + first)
                callback(it)
            }, {})
    }

    fun getHomeFromNet(callback: (List<HomeItemBean>) -> Unit) {
        if (isFirst && PAGE.isEmpty()) {
            isFirst = false
            first(callback)
        } else {
            Log.d("aaa", "getHomeFromNet else")
            other(callback)
        }
    }

    private fun first(callback: (List<HomeItemBean>) -> Unit) {
        getPageFromNet {
            getFromNet(callback)
        }
    }

    private fun other(callback: (List<HomeItemBean>) -> Unit) {
        getFromNet(callback)
    }

    private fun getFromNet(callback: (List<HomeItemBean>) -> Unit) {
        getPage {
            IdeaApi.getApiService(
                GetHomeItemService::class.java,
                false
            ).getHomeItem(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    val temp = setDirection(list)
                    addToDB(temp)
                    callback(temp)
                }, {})
        }
    }

    private fun addToDB(list: List<HomeItemBean>) {
        val homeItemDao = AppDatabase.getInstance(MyApplication.getContext()).homeItemDao()
        homeItemDao.insertHomeItems(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {})
    }

    private fun getPage(callback: (Int) -> Unit) {
        if (PAGE.isEmpty()) {
            getPageFromNet {
                val start = PAGE[0]
                if (PAGE.size != 0) {
                    PAGE.remove(start)
                }
                callback(start)
            }
        } else {
            val start = PAGE[0]
            if (PAGE.size != 0) {
                PAGE.remove(start)
            }
            callback(start)
        }
    }

    private fun setDirection(list: List<HomeItemBean>): List<HomeItemBean> {
        val arrayList = ArrayList<HomeItemBean>()
        for (h: HomeItemBean in list) {
            if (h != null) {
                arrayList.add(h)
            }
        }
        for (h: HomeItemBean in arrayList) {
            val ds = h.drec
            if (!ds.isNullOrEmpty()) {
                val sb = StringBuilder()
                sb.append(ds[0] + " ")
                sb.append(ds[1] + " ")
                sb.append(ds[2])
                h.direction = sb.toString()
            }
            val stringBuilder = StringBuilder()
            stringBuilder.append(h.graduationYear.toString())
            stringBuilder.append("年入学")
            h.graduation = stringBuilder.toString()
        }
        return arrayList
    }

    fun getHomeItemFromDBPaging(callback: (LiveData<PagedList<HomeItemBean>>) -> Unit) {
        val detailList = LivePagedListBuilder(
            homeItemDao.getHomeItem(), PagedList
                .Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(ENABLE_PLACEHOLDER).build()
        ).build()
        callback(detailList)
    }

    fun getHomeItemFromNetPaging(callback: (LiveData<PagedList<HomeItemBean>>) -> Unit) {
        val homeItemList = LivePagedListBuilder(
            PagingHomeItemDataSourceFactory(), PagedList
                .Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(ENABLE_PLACEHOLDER).build()
        ).build()
        callback(homeItemList)
    }

    fun getPageFromNet(callback: () -> Unit) {
        IdeaApi.getApiService(GetHomeItemPageService::class.java, false).getPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getArray(it.page, callback)
            }, {})
    }

    private fun getArray(page: Int, callback: () -> Unit) {
        val randNum = Random(System.currentTimeMillis())
        val array = IntArray(page)
        var i = 0
        while (i < page) {
            array[i] = (randNum.nextInt(page) + 1) % page
            for (j in 0 until i) {
                if (array[j] == array[i]) {
                    i--
                    break
                }
            }
            i++
        }
        PAGE.clear()
        for (j: Int in array) {
            PAGE.add(j)
        }
        callback()
    }

    companion object {
        private const val PAGE_SIZE = 15
        private const val ENABLE_PLACEHOLDER = true

        @Volatile
        private var instance: HomeItemRepository? = null

        fun getInstance(homeItemDao: HomeItemDao) =
            instance ?: synchronized(this) {
                instance
                    ?: HomeItemRepository(homeItemDao).also { instance = it }
            }
    }
}