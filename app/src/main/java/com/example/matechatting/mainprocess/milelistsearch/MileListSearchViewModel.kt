package com.example.matechatting.mainprocess.milelistsearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.matechatting.bean.UserBean

class MileListSearchViewModel(private val repository: MileListSearchRepository) : ViewModel() {
    val searchData = MutableLiveData<List<UserBean>>()

    fun search(key: String) {
        repository.searchByKey(key){
            searchData.value = it
        }
    }

}