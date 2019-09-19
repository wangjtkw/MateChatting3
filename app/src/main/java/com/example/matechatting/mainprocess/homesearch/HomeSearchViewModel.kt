package com.example.matechatting.mainprocess.homesearch

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.matechatting.bean.SearchBean

class HomeSearchViewModel(private val repository:HomeSearchRepository) :ViewModel(){

    fun getResult(key: String, page: Int, size: Int = 20, callback: (List<SearchBean.Payload.MyArray.Map>) -> Unit){
        Log.d("aaa","搜索的key $key")
        repository.getResult(key,page, size, callback)
    }
}