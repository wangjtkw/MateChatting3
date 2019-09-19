package com.example.matechatting.mainprocess.myinfo

import android.util.Log
import com.bumptech.glide.Glide
import com.example.matechatting.MyApplication
import com.example.matechatting.base.BaseRepository
import com.example.matechatting.bean.DirectionBean
import com.example.matechatting.bean.PostUserBean
import com.example.matechatting.bean.UserBean
import com.example.matechatting.database.DirectionDao
import com.example.matechatting.database.AccountDao
import com.example.matechatting.database.UserInfoDao
import com.example.matechatting.network.*
import com.example.matechatting.utils.PinyinUtil
import com.example.matechatting.utils.runOnNewThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MyInfoRepository(
    private val userInfoDao: UserInfoDao,
    private val accountDao: AccountDao,
    private val directionDao: DirectionDao
) : BaseRepository {

    fun saveMyInfo(userBean: UserBean, callback: () -> Unit, token: String = "") {
        saveInDB(userBean)
        saveInNet(userBean, callback, token)
    }

    private fun saveInDB(userBean: UserBean) {
        Log.d("aaa","saveInDB $userBean")
        userBean.pinyin = PinyinUtil.getFirstHeadWordChar(userBean.name)
        userInfoDao.insertUserInfo(userBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("aaa", "保存成功 $it")
            }, {
                Log.d("aaa", "MyInfoRepository saveInDB")
                it.printStackTrace()
            })
    }

    fun saveHeadImagePath(url: String) {
        if (MyApplication.getUserId() == null) {
            return
        }
        runOnNewThread {
            val target = Glide.with(MyApplication.getContext())
                .asFile()
                .load(url)
                .submit()
            val path = target.get().absolutePath
            Log.d("aaa", "saveHeadImagePath $path")
            userInfoDao.updateHeadImage(path, MyApplication.getUserId()!!)
        }
    }

    private fun saveInNet(userBean: UserBean, callback: () -> Unit = {}, token: String = "") {
        val service: UpdateUserInfoService = if (token.isEmpty()) {
            IdeaApi.getApiService(UpdateUserInfoService::class.java)
        } else {
            IdeaApi.getApiService(UpdateUserInfoService::class.java, false, OtherTokenInterceptor(token))
        }
        service.update(userBeanToPostUserBean(userBean))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("aaa", "token.isNotEmpty()" + token.isNotEmpty().toString())
                if (it.success && token.isNotEmpty()) {
                    Log.d("aaa", "saveInNet$it")
                    doOnSaveSuccess(token, callback)
                } else {
                    Log.d("aaa", "调用callback")
                    callback()
                }
            }, {})
    }

    private fun doOnSaveSuccess(token: String, callback: () -> Unit) {
        Log.d("aaa", "token $token")
        accountDao.getAllByToken(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("aaa", "doOnSaveSuccess$it")
                saveState(it.account, it.token, it.id, it.inSchool)
                updateFirst(callback)
            }, {})
    }

    private fun updateFirst(callback: () -> Unit) {
        IdeaApi.getApiService(UpdateFirstService::class.java).updateFirst()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.success) {
                    callback()
                }
            }, {})
    }

    private fun saveState(account: String, token: String, id: Int, inSchool: Boolean) {
        MyApplication.saveLoginState(account, token, id, inSchool)
    }

    private fun userBeanToPostUserBean(userBean: UserBean): PostUserBean {
        val postUserBean = PostUserBean()
        userBean.apply {
            postUserBean.city = city
            postUserBean.company = company
            postUserBean.email = email
            postUserBean.job = job
            postUserBean.qqAccount = qqAccount
            postUserBean.slogan = slogan
            postUserBean.wechatAccount = wechatAccount
        }
        return postUserBean
    }


    fun getMyInfoFromNet(callback: (UserBean) -> Unit, token: String = "") {
        val service: GetMyInfoService = if (token.isEmpty()) {
            IdeaApi.getApiService(GetMyInfoService::class.java)
        } else {
            IdeaApi.getApiService(GetMyInfoService::class.java, false, OtherTokenInterceptor(token))
        }
        service.getMyInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val info = setInfo(it)
                saveInDB(info)
                callback(info)
            }, {
                getMyInfoFromDB(callback)
            })
    }

    fun getMyInfoFromDB(callback: (UserBean) -> Unit) {
        val id = MyApplication.getUserId() ?: 0
        Log.d("aaa", "id $id")
        userInfoDao.getUserInfo(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
                Log.d("aaa", "getMyInfoFromDB $user")
                callback(user)
            }, {
                it.printStackTrace()
            })

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
            val sb = StringBuilder()
            sb.append(graduationYear)
            sb.append("年入学")
            graduation = sb.toString()
        }
        return userBean
    }

    fun getAllBigDirection(parentId: Int, callback: () -> Unit) {
        directionDao.selectDirectionByParent(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                callback()
            })
    }

    fun getDirectionByName(directionName: String, callback: (DirectionBean) -> Unit) {
        directionDao.selectDirectionByName(directionName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it)
            }, {
                callback(DirectionBean(0))
            })
    }

    fun getDirectionById(id: Int, callback: (DirectionBean) -> Unit) {
        directionDao.selectDirectionById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback(it)
            }, {})
    }

    fun updateDirection(directionBean: DirectionBean, callback: () -> Unit = {}) {
        directionDao.insertDirection(directionBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {})
    }


    companion object {
        @Volatile
        private var instance: MyInfoRepository? = null

        fun getInstance(userInfoDao: UserInfoDao, accountDao: AccountDao, directionDao: DirectionDao) =
            instance ?: synchronized(this) {
                instance ?: MyInfoRepository(userInfoDao, accountDao, directionDao).also { instance = it }
            }
    }
}