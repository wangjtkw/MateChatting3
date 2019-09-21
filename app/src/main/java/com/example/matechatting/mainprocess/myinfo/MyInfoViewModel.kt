package com.example.matechatting.mainprocess.myinfo

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.matechatting.BASE_URL
import com.example.matechatting.PATH
import com.example.matechatting.bean.UserBean
import com.example.matechatting.mainprocess.direction.DirectionNewActivity

class MyInfoViewModel(private val repository: MyInfoRepository) : ViewModel() {
    val myInfoName = ObservableField<String>("未填写")
    val myInfoMajor = ObservableField<String>("未填写")
    val myInfoGraduate = ObservableField<String>("未填写")
    val myInfoCompany = ObservableField<String>("未填写")
    val myInfoJob = ObservableField<String>("未填写")
    val myInfoDirection = ObservableField<String>("未填写")
    val myInfoQQ = ObservableField<String>("未填写")
    val myInfoWeixin = ObservableField<String>("未填写")
    val myInfoEmile = ObservableField<String>("未填写")
    val myInfoCity = ObservableField<String>("未填写")
    val myInfoSlogan = ObservableField<String>("未填写")
    val myInfoHeadImage = ObservableField<String>("")
    val myInfoString = "未填写"
    val myInfoDefailtSlogan = "快乐生活每一天"

    fun getMyInfo(callback: (UserBean) -> Unit, token: String = "") {
        if (token.isEmpty()) {
            repository.getMyInfoFromDB {
                setInfoDB(it)
                callback(it)
            }
        } else {
            repository.getUserBeanFromNet (token) {
                Log.d("aaa","第一次登陆返回数据 $it")
                setInfoNet(it)
                callback(it)
            }
        }
    }

    private fun setInfoDB(userBean: UserBean) {
        userBean.apply {
            myInfoName.set(name)
            myInfoMajor.set(majorName)
            myInfoGraduate.set(graduation)
            myInfoCompany.set(company)
            myInfoJob.set(job)
            myInfoDirection.set(direction)
            if (qqAccount.toString().isEmpty() || qqAccount == 0L) {
                myInfoQQ.set("")
            } else {
                myInfoQQ.set(qqAccount.toString())
            }
            myInfoWeixin.set(wechatAccount)
            myInfoEmile.set(email)
            myInfoCity.set(city)
            myInfoSlogan.set(slogan)
//            if (!headImage.isNullOrEmpty()) {
//                myInfoHeadImage.set(headImage)
//                myInfoHeadImage.notifyChange()
//            }
        }
    }

    fun getDirection(direction: String, callback: () -> Unit = {}) {
        if (direction.isEmpty()) {
            callback()
            return
        } else {
            Log.d("aaa","方向字符串 $direction")
            val a = direction.split(" ")
            Log.d("aaa","方向字符串拆分 $a")
            for ((i, str: String) in a.withIndex()) {
                repository.getDirectionByName(str) { small ->
                    if (small.directionName.isNotEmpty()) {
                        DirectionNewActivity.saveMap.put(small.id, small.bigDirectionId)
                        DirectionNewActivity.resultMap.put(small.id,small.directionName)
                        small.isSelect = true
                        repository.updateDirection(small)
                        repository.getDirectionById(small.bigDirectionId) { big ->
                            big.isSelect = true
                            repository.updateDirection(big)
                        }
                    }
                }
                if (i == a.size - 1){
                    callback()
                }
            }
        }
    }

    private fun setInfoNet(userBean: UserBean) {
        userBean.apply {
            myInfoName.set(name)
            myInfoMajor.set(majorName)
            myInfoGraduate.set(graduation)
            myInfoCompany.set(company)
            myInfoJob.set(job)
            myInfoDirection.set(direction)
            if (qqAccount.toString().isEmpty() || qqAccount == 0L) {
                myInfoQQ.set("")
            } else {
                myInfoQQ.set(qqAccount.toString())
            }
            myInfoWeixin.set(wechatAccount)
            myInfoEmile.set(email)
            myInfoCity.set(city)
            myInfoSlogan.set(slogan)
            if (!headImage.isNullOrEmpty()) {
                myInfoHeadImage.set(headImage)
            }
        }
    }

    fun saveData(userBean: UserBean, callback: () -> Unit={}, token: String = "") {
        repository.saveMyInfo(userBean, callback, token)
    }
}