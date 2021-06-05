package com.example.matechatting.mainprocess.myinfo

import android.util.Log
import com.bumptech.glide.Glide
import com.example.matechatting.BASE_URL
import com.example.matechatting.MORE_BASE
import com.example.matechatting.MyApplication
import com.example.matechatting.PATH
import com.example.matechatting.base.BaseRepository
import com.example.matechatting.bean.DirectionBean
import com.example.matechatting.bean.PostUserBean
import com.example.matechatting.bean.UserBean
import com.example.matechatting.database.DirectionDao
import com.example.matechatting.database.AccountDao
import com.example.matechatting.database.UserInfoDao
import com.example.matechatting.network.*
import com.example.matechatting.utils.ExecuteObserver
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

    fun getUserBeanFromNet(token: String = "", callback: (UserBean) -> Unit = {}) {
        val service: GetMyInfoService = if (token.isEmpty()) {
            IdeaApi.getApiService(GetMyInfoService::class.java)
        } else {
            IdeaApi.getApiService(GetMyInfoService::class.java, false, OtherTokenInterceptor(token))
        }
        service.getMyInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                Log.d(TAG, "getUserBeanFromNet 网络请求返回UserBean -> $it")
                setUserBeanInfo(it, 1, token, callback)
            }))
    }

    /**
     *设置UserBean的信息，并调用存储
     * @param state:该UserBean是什么类型的
     * @param state:0(陌生人)，1（自己），2（新好友），3（聊天好友），4（好友）
     * @param userBean:网络请求得到的数据类
     */
    private fun setUserBeanInfo(userBean: UserBean, state: Int, token: String = "", callback: (UserBean) -> Unit = {}) {
        //设置UserBean的类型
        var saveTemp = setState(userBean, state)
        //提取UserBean姓名的首字母
        saveTemp = setFirstPinYin(saveTemp)
        //设置UserBean的方向及入学年
        saveTemp = setInfo(saveTemp)
        //如果有头像信息，则缓存入本地，并返回缓存路径
//        if (!saveTemp.headImage.isNullOrEmpty()) {
//            saveHeadImagePath(saveTemp) {
//                if (token.isEmpty()) {
//                    saveInDB(it)
//                }
//                callback(it)
//            }
//        } else {
            if (token.isEmpty()) {
                saveInDB(saveTemp)
            }
            callback(saveTemp)
//        }
    }

    /**
     * 将UserBean数据类存入数据库
     * @param userBean:基本信息完全的数据类
     */
    private fun saveInDB(userBean: UserBean) {
        userInfoDao.insertUserInfo(userBean)
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

    /**
     * 将网络请求的UserBean中的@link [UserBean.directions] 和 @link [UserBean.graduationYear]
     * 转化拼接为在界面的字符串，存入@link [UserBean.direction] 和 @link[UserBean.graduation]
     * @param result:信息未改变的UserBean
     * @return 信息更改后的UserBean
     */
    private fun setInfo(result: UserBean): UserBean {

        result.directions?.apply {
            val sb = StringBuilder()
            for (s: String in this) {
                sb.append(" ")
                sb.append(s)
            }
            result.direction = sb.toString().trim()
        }
        if (!result.responseAwards.isNullOrEmpty()) {
            val sb = java.lang.StringBuilder()
            for (s: String in result.responseAwards!!) {
                sb.append(" ")
                sb.append(s)
            }
            result.award = sb.toString()
        }
        val sb = StringBuilder()
        sb.append(result.graduationYear)
        sb.append("年入学")
        result.graduation = sb.toString()
        Log.d(TAG, "setInfo 传入的方向 -> ${result.directions}")
        Log.d(TAG, "setInfo 转化后的方向 -> ${result.direction}")
        Log.d(TAG, "setInfo 传入的入学年 -> ${result.graduationYear}")
        Log.d(TAG, "setInfo 转化后的入学年 -> ${result.graduation}")
        return result
    }

    /**
     * 将从服务器获取来的头像图片链接用Glide缓存到本地，并将缓存路径放入UserBean的头像路径中
     *@param result:从服务器获取来的数据类，用于获取其头像路径，需进行拼接后才可进行Glide加载
     * @param callback:将更改了头像路径的UserBean返回
     */
    private fun saveHeadImagePath(result: UserBean, callback: (UserBean) -> Unit) {
        if (result.headImage.isNullOrEmpty()) {
            return
        }
        val start = result.headImage!!.startsWith("/data/user/0/")
        if (start) {
            return
        }
        val sb = StringBuilder()
        sb.append(BASE_URL)
            .append(MORE_BASE)
            .append(PATH)
            .append(result.headImage)
        runOnNewThread {
            val target = Glide.with(MyApplication.getContext())
                .asFile()
                .load(sb.toString())
                .submit()
            val cachePath = target.get().absolutePath
            Log.d(TAG, "saveHeadImagePath 头像缓存路径 -> $cachePath")
            result.headImage = cachePath
            callback(result)
        }
    }

    /**
     * 设置UserBean的状态
     * @param result: 需要更新的UserBean
     * @param state:需要更新成什么状态
     * @param state:0(陌生人)，1（自己），2（新好友），3（聊天好友），4（好友）
     */
    private fun setState(result: UserBean, state: Int): UserBean {
        result.state = state
        return result
    }

    /**
     * 获取UserBean姓名的首字母
     * @param result:需要获取首字母的UserBean
     */
    private fun setFirstPinYin(result: UserBean): UserBean {
        result.pinyin = PinyinUtil.getFirstHeadWordChar(result.name)
        return result
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
        private const val TAG = "MyInfoRepository"
        @Volatile
        private var instance: MyInfoRepository? = null

        fun getInstance(userInfoDao: UserInfoDao, accountDao: AccountDao, directionDao: DirectionDao) =
            instance ?: synchronized(this) {
                instance ?: MyInfoRepository(userInfoDao, accountDao, directionDao).also { instance = it }
            }
    }
}