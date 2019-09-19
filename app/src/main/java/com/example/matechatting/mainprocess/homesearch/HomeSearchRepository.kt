package com.example.matechatting.mainprocess.homesearch

import android.util.Log
import com.example.matechatting.base.BaseRepository
import com.example.matechatting.bean.SearchBean
import com.example.matechatting.network.IdeaApi
import com.example.matechatting.network.SearchService
import com.example.matechatting.utils.runOnNewThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeSearchRepository : BaseRepository {

    fun getResult(key: String, page: Int, size: Int = 20, callback: (List<SearchBean.Payload.MyArray.Map>) -> Unit) {
        IdeaApi.getApiService(SearchService::class.java).getResult(key, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.success) {
                    Log.d("aaa", "getResult" + it.toString())
                    callback(setInfo(it.payload))
                } else {
                    callback(ArrayList())
                }
            }, {
                callback(ArrayList())
            })
    }




    private fun setBeanInfo(userBean: SearchBean.Payload.MyArray.Map) {
        runOnNewThread {
            userBean.apply {
                if (!directions.myArrayList.isNullOrEmpty()) {
                    val sb = StringBuilder()
                    for (s: String in directions.myArrayList) {
                        sb.append(" ")
                        sb.append(s)
                    }
                    direction = sb.toString()
                }
                val sb = StringBuilder()
                sb.append(graduationYear)
                sb.append("年入学")
                graduation = sb.toString()
            }
        }

//        return userBean
    }

    private fun setInfo(resultBean: SearchBean.Payload): ArrayList<SearchBean.Payload.MyArray.Map> {
        val array = ArrayList<SearchBean.Payload.MyArray.Map>()
        if (resultBean.empty) {
            return array
        }
        val dataList = resultBean.myArrayList
        for (b: SearchBean.Payload.MyArray in dataList) {
            if (!b.empty) {
                array.add(b.map)
            }
        }
        for (a: SearchBean.Payload.MyArray.Map in array) {
            setBeanInfo(a)
        }
        return array
    }
}