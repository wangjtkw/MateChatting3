package com.example.matechatting.mainprocess.direction

import android.util.Log
import android.util.SparseIntArray
import androidx.core.util.forEach
import androidx.core.util.isEmpty
import androidx.core.util.isNotEmpty
import androidx.lifecycle.ViewModel
import com.example.matechatting.MyApplication
import com.example.matechatting.bean.BigDirectionBean
import com.example.matechatting.bean.PostDirectionBean
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.isNetworkConnected

class DirectionActivityViewModel(private val repository: DirectionActivityRepository) : ViewModel() {

    fun getBigDirection(callback: (List<BigDirectionBean>) -> Unit) {
//        if (isNetworkConnected(MyApplication.getContext()) == NetworkState.NONE) {
        repository.getBigDirectionFromDB {
            if (!it.isNullOrEmpty()) {
                Log.d("bbb", "数据库有")
                callback(it)
            } else {
                Log.d("bbb", "数据库没")
                repository.getBigDirectionFromNet(callback)
            }
        }
//        } else {
//            repository.getBigDirectionFromNet(callback)
//        }
    }

    fun saveDirection(saveTemp: SparseIntArray?, token: String, callback: () -> Unit) {
        val small = ArrayList<Int>()
        if (saveTemp != null && saveTemp.isNotEmpty()) {
            Log.d("aaa", "savetemp $saveTemp")
            saveTemp.forEach { key, value ->
                repository.updateDirectionState(false, key)
                repository.updateDirectionState(false, value)
            }
        }
        DirectionNewActivity.saveMap.forEach { key: Int, value: Int ->
            repository.updateDirectionState(true, key)
            repository.updateDirectionState(true, value)
            small.add(key)
        }
        val postDirectionBean = PostDirectionBean(small)
        Log.d("aaa", "saveDirection $small")
        repository.saveDirection(postDirectionBean, token, callback)

    }
}