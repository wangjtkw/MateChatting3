package com.example.matechatting.mainprocess.direction

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.matechatting.bean.SaveDirectionBean

class DirectionFragmentViewModel(private val repository: DirectionFragmentRepository) : ViewModel() {

    fun getSmallDirection(id: Int, callback: (SaveDirectionBean) -> Unit) {
//        if (isNetworkConnected(MyApplication.getContext()) == NetworkState.NONE){
            repository.selectDirection(id){
                if (it.normalDirectionList.isNullOrEmpty()){
                    Log.d("aaa","savedirectionbean $it")
                    repository.getSmallDirection(id,callback)
                }else{
                    callback(it)
                }
            }
//        }else{
//            repository.getSmallDirection(id,callback)
//        }
    }
}