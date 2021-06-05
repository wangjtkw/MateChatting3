package com.example.matechatting.mainprocess.mine

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.matechatting.BASE_URL
import com.example.matechatting.MyApplication
import com.example.matechatting.PATH
import com.example.matechatting.bean.UserBean
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.isNetworkConnected

class MineViewModel(private val repository: MineRepository) : ViewModel() {
    val mineName = ObservableField("未登录")
    val mineSlogan = ObservableField("快乐生活每一天")
    val mineHeadImage = ObservableField("")
    val defaultSlogan = "快乐生活每一天"
    val defaultName = "未登录"

    fun getMine() {
        if (isNetworkConnected(MyApplication.getContext()) == NetworkState.NONE) {
        repository.getMineFromDB {
            Log.d("aaa", "数据 $it")
            setInfoDB(it)
        }
        } else {
            repository.getMineFromNet {
                setInfoDB(it)
            }
        }
    }

    private fun setInfoDB(userBean: UserBean) {
        userBean.apply {
            if (name.isEmpty()) {
                mineName.set(defaultName)
            } else {
                mineName.set(name)
            }
            if (slogan.isEmpty()) {
                mineSlogan.set(defaultSlogan)
            } else {
                mineSlogan.set(slogan)
            }
            if (headImage.isNullOrEmpty()) {
                Log.d("aaa","head image $headImage")
                Log.d("aaa","Mine user id ${MyApplication.getUserId()}")
                mineHeadImage.set(headImage)
                mineHeadImage.notifyChange()
            }else{
                mineHeadImage.set(headImage)
            }
        }
    }

    private fun setInfoNet(userBean: UserBean) {
        userBean.apply {
            if (name.isEmpty()) {
                mineName.set(defaultName)
            } else {
                mineName.set(name)
            }
            if (slogan.isEmpty()) {
                mineSlogan.set(defaultSlogan)
            } else {
                mineSlogan.set(slogan)
            }
            if (!headImage.isNullOrEmpty()) {
                val sb = StringBuilder()
                sb.append(BASE_URL)
                sb.append(PATH)
                sb.append(headImage)
                mineHeadImage.set(sb.toString())
                repository.saveHeadImagePath(sb.toString())
            }
        }
    }


}