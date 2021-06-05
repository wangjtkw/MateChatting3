package com.example.matechatting.mainprocess.main

import android.util.Log
import com.bumptech.glide.Glide
import com.example.matechatting.BASE_URL
import com.example.matechatting.MORE_BASE
import com.example.matechatting.MyApplication
import com.example.matechatting.PATH
import com.example.matechatting.bean.UserBean
import com.example.matechatting.database.UserInfoDao
import com.example.matechatting.network.*
import com.example.matechatting.utils.PinyinUtil
import com.example.matechatting.utils.runOnNewThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainRepository(private val userInfoDao: UserInfoDao) {

    fun getMineFromNet() {
        IdeaApi.getApiService(GetMineService::class.java).getMine()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("aaa", "main 个人信息 ${it.directions}")
                val info = setInfo(it)
                info.state = 1
                if (!info.headImage.isNullOrEmpty()) {
                    saveHeadImagePath(info) { path ->
                        info.headImage = path
                        Log.d("aaa","网络获取信息 $info")
                        saveInDB(info)
                    }
                } else {
                    Log.d("aaa","直接存入数据 $info")
                    saveInDB(info)
                }
            }, {})
    }

    fun saveHeadImagePath(info: UserBean, callback: (String) -> Unit) {
        val sb = StringBuilder()
        sb.append(BASE_URL)
            .append(MORE_BASE)
            .append(PATH)
            .append(info.headImage)
        if (MyApplication.getUserId() == null) {
            return
        }
        runOnNewThread {
            val target = Glide.with(MyApplication.getContext())
                .asFile()
                .load(sb.toString())
                .submit()
            val path = target.get().absolutePath
            Log.d("aaa", "main path $path")
            callback(path)
        }
    }

    private fun saveInDB(userBean: UserBean) {
        userBean.pinyin = PinyinUtil.getFirstHeadWordChar(userBean.name)
        userInfoDao.insertUserInfo(userBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("aaa", "信息保存成功 ${userBean.direction} + $userBean")
            }, {})
    }

    fun getAllFriendIdFromNet(callback: () -> Unit) {
        IdeaApi.getApiService(GetAllFriendIdService::class.java).getAllFriendId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ net ->
                getAllFriendFromNet(callback)
            }, {})
    }

    fun getIdFromDB(callback: (List<Int>) -> Unit) {
        userInfoDao.getAllFriendId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it)
                Log.d("aaa", "数据库查询 $it")
            }, {
                val array = ArrayList<Int>()
                callback(array)
            })
    }

    fun getUserById(id: Int, callback: () -> Unit = {}) {
        IdeaApi.getApiService(GetUserByIdService::class.java).getUser(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val bean = setInfo(it)
                if (!bean.headImage.isNullOrEmpty()) {
                    saveHeadImagePath(bean) {
                        updateState(bean, 4)
                    }
                }
                updateState(bean, 4)
            }, {})
    }

    fun getAllFriendFromNet(callback: () -> Unit) {
        IdeaApi.getApiService(GetAllFriendService::class.java).getAllFriend()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                for (bean: UserBean in it) {
                    if (bean != null) {
                        val temp = setInfo(bean)
//                        bean.pinyin = PinyinUtil.getPinyin(bean.name)
                        if (!bean.headImage.isNullOrEmpty()) {
                            saveHeadImagePath(bean) {
                                updateState(bean, 4)
                            }
                        }
                        updateState(temp, 4)
                    }
                }
                callback()
            }, {})
    }

    fun updateState(userBean: UserBean, state: Int, callback: () -> Unit = {}) {
        userBean.state = state
        userBean.pinyin = PinyinUtil.getFirstHeadWordChar(userBean.name)
        userInfoDao.insertUserInfo(userBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }

    private fun setInfo(userBean: UserBean): UserBean {
        userBean.apply {
            if (!directions.isNullOrEmpty()) {
                val sb = StringBuilder()
                for (s: String in directions!!) {
                    sb.append(" ")
                    sb.append(s)
                }
                direction = sb.toString().trim()
            }
            if (!responseAwards.isNullOrEmpty()) {
                val sb = java.lang.StringBuilder()
                for (s: String in responseAwards!!) {
                    sb.append(" ")
                    sb.append(s)
                }
                award = sb.toString()
            }
            val sb = StringBuilder()
            sb.append(graduationYear)
            sb.append("年入学")
            graduation = sb.toString()
        }
        return userBean
    }

    companion object {
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(userInfoDao: UserInfoDao) =
            instance ?: synchronized(this) {
                instance ?: MainRepository(userInfoDao).also { instance = it }
            }
    }
}