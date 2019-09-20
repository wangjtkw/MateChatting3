package com.example.matechatting.mainprocess.homesearch

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.matechatting.MyApplication
import com.example.matechatting.bean.SearchBean
import com.example.matechatting.mainprocess.repository.UserBeanRepository
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.isNetworkConnected

class HomeSearchViewModel(
    private val repository: HomeSearchRepository,
    private val userBeanRepository: UserBeanRepository
) : ViewModel() {

    fun getResult(key: String, page: Int, size: Int = 20, callback: (List<SearchBean.Payload.MyArray.Map>) -> Unit) {
        Log.d("aaa", "搜索的key $key")
        repository.getResult(key, page, size, callback)
    }

    fun getUserInfo(id: Int, callback: (Boolean) -> Unit) {
        userBeanRepository.getUserBeanFromDB(id) {
            if (isNetworkConnected(MyApplication.getContext()) == NetworkState.NONE) {
                callback(false)
                return@getUserBeanFromDB
            }
            if (it.stuId != "" && it.state != 0) {
                callback(true)
            } else {
                userBeanRepository.getUserById(id, 0) {
                    callback(true)
                }
            }
        }
    }
}