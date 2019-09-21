package com.example.matechatting.tcpprocess.repository

import android.util.Log
import com.bumptech.glide.Glide
import com.example.matechatting.BASE_URL
import com.example.matechatting.MORE_BASE
import com.example.matechatting.MyApplication
import com.example.matechatting.PATH
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.bean.HasMessageBean
import com.example.matechatting.bean.UserBean
import com.example.matechatting.database.AppDatabase
import com.example.matechatting.mainprocess.chatting.ChattingActivity.Companion.id
import com.example.matechatting.mainprocess.repository.UserBeanRepository
import com.example.matechatting.network.GetAllFriendService
import com.example.matechatting.network.GetOnlineStateService
import com.example.matechatting.network.GetUserByIdService
import com.example.matechatting.network.IdeaApi
import com.example.matechatting.utils.ExecuteObserver
import com.example.matechatting.utils.PinyinUtil
import com.example.matechatting.utils.runOnNewThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object TCPRepository {

    private const val TAG = "TCPRepository"

    fun getLoginStateById(id: Int, callback: (Boolean) -> Unit) {
        IdeaApi.getApiService(GetOnlineStateService::class.java).updateDirection(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it.online)
            }, {})
    }

    fun getUserInfo(id: Int, callback: (Boolean) -> Unit) {
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().getUserInfo(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(true)
            }, {
                callback(false)
            })
    }

    fun getAndSaveFriendInfo(state: Int, id: Int, callback: (UserBean) -> Unit) {
        IdeaApi.getApiService(GetUserByIdService::class.java).getUser(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                setUserBeanInfo(it, state, "", callback)
            }))
    }

    fun changeHasMessage(userId: Int, otherId: Int, callback: () -> Unit = {}) {
        val hasMessageBean = HasMessageBean(otherId,userId,true)
        AppDatabase.getInstance(MyApplication.getContext()).hasMessageDao().insertUserInfo(hasMessageBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                callback()
            }
            .doOnError {

            }
            .subscribe()
    }

    fun changeUserState(state: Int, id: Int, callback: () -> Unit) {
        Log.d("aaa", "changeUserState 调用")
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().updateStateById(state, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }

    private fun setUserBeanInfo(userBean: UserBean, state: Int, token: String = "", callback: (UserBean) -> Unit = {}) {
        //设置UserBean的类型
        var saveTemp = setState(userBean, state)
        //提取UserBean姓名的首字母
        saveTemp = setFirstPinYin(saveTemp)
        //设置UserBean的方向及入学年
        saveTemp = setInfo(saveTemp)
        //如果有头像信息，则缓存入本地，并返回缓存路径

        if (!saveTemp.headImage.isNullOrEmpty()) {
            saveHeadImagePath(saveTemp) {
                Log.d(TAG, "setUserBeanInfo 调用")
                if (token.isEmpty()) {
                    saveInDB(it)
                }
                callback(it)
            }
        } else {
            if (token.isEmpty()) {
                saveInDB(saveTemp)
            }
            callback(saveTemp)
        }
    }

    fun saveInDB(userBean: UserBean) {
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().insertUserInfo(userBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "saveInDB 数据库保存成功")
            }
            .doOnError {
                Log.d(TAG, "saveInDB 数据库存储错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    private fun saveHeadImagePath(result: UserBean, callback: (UserBean) -> Unit) {
        if (result.headImage.isNullOrEmpty()) {
            return
        }
        val start = result.headImage!!.startsWith("/data/user/0/")
        if (start) {
            return
        }
        Log.d(TAG, "saveHeadImagePath 调用")
        val sb = StringBuilder()
        sb.append(BASE_URL)
            .append(MORE_BASE)
            .append(PATH)
            .append(result.headImage)
        Thread(Runnable {
            Log.d(TAG, "saveHeadImagePath 调用")
            val target = Glide.with(MyApplication.getContext())
                .asFile()
                .load(sb.toString())
                .submit()
            val cachePath = target.get().absolutePath
            Log.d(TAG, "saveHeadImagePath 头像缓存路径 -> $cachePath")
            result.headImage = cachePath
            callback(result)
        }).start()
    }

    fun getAllFriendFromNet(callback: () -> Unit) {
        IdeaApi.getApiService(GetAllFriendService::class.java).getAllFriend()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                Log.d(TAG, "getAllFriendFromNet 从服务器获取的所有好友信息 -> $it")
                for ((i, bean: UserBean) in it.withIndex()) {
                    //怕服务器返回null，所以加的判断
                    Log.d(TAG, "getAllFriendFromNet $i + ${it.size}")
                    if (bean != null) {
                        setUserBeanInfo(bean, 4) { user ->
                            if (i == it.size - 1) {
                                Log.d(TAG, "getAllFriendFromNet 加载好友完毕")
                                callback()
                            }
                        }
                    }
                }
//                callback()
            }, onExecuteError = {
                it.printStackTrace()
            }))
    }

    private fun setFirstPinYin(result: UserBean): UserBean {
        result.pinyin = PinyinUtil.getFirstHeadWordChar(result.name)
        return result
    }

    private fun setState(result: UserBean, state: Int): UserBean {
        result.state = state
        return result
    }

    private fun setInfo(userBean: UserBean): UserBean {
        userBean.apply {
            if (!directions.isNullOrEmpty()) {
                val sb = StringBuilder()
                for (s: String in directions!!) {
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
        return userBean
    }

    fun saveNewFriendInDB(userBean: UserBean, callback: () -> Unit) {
        userBean.pinyin = PinyinUtil.getFirstHeadWordChar(userBean.name)
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().insertUserInfo(userBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }

    fun saveMessage(chattingBean: ChattingBean, callback: () -> Unit) {
        AppDatabase.getInstance(MyApplication.getContext()).chattingDao().insertChattingBean(chattingBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }

    fun updateOnLineState(state: Boolean, id: Int, callback: () -> Unit = {}) {
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().updateOnLineState(state, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                callback()
                Log.d("aaa", "更新成功")
            }
            .doOnError {
                it.printStackTrace()
            }
            .subscribe()
    }

    fun updateOnLineStateList(state: Boolean, ids: List<Int>, callback: () -> Unit = {}) {
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().updateOnlineStateList(state, ids)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                callback()
                Log.d("aaa", "更新成功")
            }
            .doOnError {
                it.printStackTrace()
            }
            .subscribe()
    }

    fun getAllIdFromDB(callback: (List<Int>) -> Unit) {
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().getAllFriendId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it)
            }, {})
    }

    fun getAllFriendFromDB(callback: (List<UserBean>) -> Unit) {
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().getAllFriend()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(callback)
            .doOnError {

            }
            .subscribe()
    }

    fun updateState(state: Int, id: Int, callback: () -> Unit = {}) {
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().updateState(state, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                callback()
            }
            .doOnError {

            }
            .subscribe()
    }

}