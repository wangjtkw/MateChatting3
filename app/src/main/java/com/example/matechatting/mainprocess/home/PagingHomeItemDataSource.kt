package com.example.matechatting.mainprocess.home

import androidx.paging.PageKeyedDataSource
import com.example.matechatting.MyApplication
import com.example.matechatting.PAGE
import com.example.matechatting.bean.HomeItemBean
import com.example.matechatting.database.AppDatabase
import com.example.matechatting.network.GetHomeItemService
import com.example.matechatting.network.IdeaApi
import com.example.matechatting.utils.ExecuteOnceObserver
import com.example.matechatting.utils.runOnNewThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PagingHomeItemDataSource : PageKeyedDataSource<Int, HomeItemBean>() {

    private lateinit var getHomeItemService: GetHomeItemService

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, HomeItemBean>) {
        getHomeItemService = IdeaApi.getApiService(GetHomeItemService::class.java, false)
        getHomeItemService.getHomeItem(getPage())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                val list = setDirection(it)
                addToDB(list)
                callback.onResult(list, getPage(), getPage())
            }))
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, HomeItemBean>) {
        getHomeItemService = IdeaApi.getApiService(
            GetHomeItemService::class.java,
            false
        )
        getHomeItemService.getHomeItem(params.key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                val list = setDirection(it)
                addToDB(list)
                callback.onResult(list, getPage())
            }))
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, HomeItemBean>) {}

    private fun addToDB(list: List<HomeItemBean>) {
        val homeItemDao = AppDatabase.getInstance(MyApplication.getContext()).homeItemDao()
        runOnNewThread {
            homeItemDao.insertHomeItems(list)
        }
    }

    private fun getPage(): Int {
        val start = PAGE[0]
        PAGE.remove(start)
        return start
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
            stringBuilder.append("年毕业生")
            h.graduation = stringBuilder.toString()
        }
        return arrayList
    }
}