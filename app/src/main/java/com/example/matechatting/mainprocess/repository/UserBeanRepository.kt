package com.example.matechatting.mainprocess.repository

import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.matechatting.BASE_URL
import com.example.matechatting.MORE_BASE
import com.example.matechatting.MyApplication
import com.example.matechatting.PATH
import com.example.matechatting.bean.PostUserBean
import com.example.matechatting.bean.UserBean
import com.example.matechatting.database.UserInfoDao
import com.example.matechatting.network.*
import com.example.matechatting.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserBeanRepository(private val userInfoDao: UserInfoDao) {

    fun getUserBean(id: Int, state: Int, callback: (UserBean) -> Unit) {
        if (isNetworkConnected(MyApplication.getContext()) == NetworkState.NONE) {
            getUserBeanFromDB(id, callback)
        } else {
            getUserById(id, state, callback)
        }
    }

    /**
     * 根据UserID从数据库获取用户信息
     * @param id:用户ID
     * @param callback:将得到的信息返回
     */
    fun getUserBeanFromDB(id: Int, callback: (UserBean) -> Unit) {
        Log.d(TAG, "getUserBeanFromDB 传入的UserID -> $id")
        userInfoDao.getUserInfo(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "getUserBeanFromDB 获取用户信息成功 -> $it + ${it.direction} + ${it.graduation}")
                callback(it)
            }
            .doOnError {
                Log.d(TAG, "getUserBeanFromDB 获取用户信息错误")
                Log.d(TAG, it?.message ?: "")
                callback(UserBean())
            }
            .subscribe()
    }

    /**
     * 通过网络请求从服务器获取当前用户信息
     * 获取当前用户信息专用
     * @param token:当为第一次登录时，传入token，否则为empty，非必须参数
     * @param callback:返回数据类，也可当作执行结束的条件
     */
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
    private fun setUserBeanInfo(
        userBean: UserBean,
        state: Int,
        token: String = "",
        callback: (UserBean) -> Unit = {}
    ) {
        Log.d(TAG, "token:$token")
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
        Log.d(TAG, "saveInDB run")
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
        result.responseAwards?.apply {
            val sb = StringBuilder()
            for (s: String in this) {
                sb.append(" ")
                sb.append(s)
            }
            result.award = sb.toString().trim()
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
        Log.d(TAG, "saveHeadImagePath run")
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
        Log.d(TAG, "imgUrl:$sb")
//        runOnNewThread {
        val target = Glide.with(MyApplication.getContext())
            .downloadOnly()
            .load(sb.toString())
            .submit()
        val cachePath = target.get().absolutePath
        Log.d(TAG, "saveHeadImagePath 头像缓存路径 -> $cachePath")
        result.headImage = cachePath
        callback(result)
//        }
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

    /**
     * 更新用户状态
     * @param userBean:需要更新状态的UserBean
     * @param state:需要更新成什么状态
     * @param state:0(陌生人)，1（自己），2（新好友），3（聊天好友），4（好友）
     */
    fun updateState(userBean: UserBean, state: Int, callback: () -> Unit = {}) {
        var saveTemp = setState(userBean, state)
        saveTemp = setFirstPinYin(saveTemp)
        userInfoDao.insertUserInfo(saveTemp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "updateState 状态更新成功")
                callback()
            }
            .doOnError {
                Log.d(TAG, "updateState 状态更新错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    /**
     * 从服务器同步用户的所有好友
     * @param callback:同步完成后调用
     */
    fun getFriends(callback: () -> Unit) {
        Log.d(TAG, "getFriend 调用")
        getFriendsInfo(callback)
    }

    /**
     * 从数据库获取用户的所有好友的ID
     * @param callback:返回该用户所有好友的ID
     */
    private fun getFriendsIdFromDB(callback: (List<Int>) -> Unit) {
        userInfoDao.getAllFriendId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "getFriendsIdFromDB 获取到的所有好友ID -> $it")
                callback(it)
            }
            .doOnError {
                Log.d(TAG, "getFriendsIdFromDB 获取所有好友ID出错")
                it.printStackTrace()
                val array = ArrayList<Int>()
                callback(array)
            }
            .subscribe()
    }

    /**
     * 从服务器获取用户的所有好友的ID
     * @param callback:返回该用户所有好友的ID
     */
    private fun getFriendsIdFromNet(callback: (List<Int>) -> Unit) {
        IdeaApi.getApiService(GetAllFriendIdService::class.java).getAllFriendId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                Log.d(TAG, "getFriendsIdFromNet 获取的所有用户ID -> ${it.payload}")
                callback(it.payload)
            }))
    }

    /**
     * 开始进行同步，当本地数据库好友数与服务器好友数相差小于5时调用@link [getUserById]
     * @link [getUserById] 在10s内只能进行总计10次请求
     * @link [getAllFriendFromNet] 可返回所有好友信息
     */
    private fun getFriendsInfo(callback: () -> Unit) {
        getAllFriendFromNet(callback)
    }

    /**
     * 根据ID从服务器获取该用户的信息，10s之内最多10次
     * @param id:用户ID
     * @param state:需要存储成什么状态
     * @param state:0(陌生人)，1（自己），2（新好友），3（聊天好友），4（好友）
     * @param callback:获取完成后调用，非必要参数
     */
    fun getUserById(id: Int, state: Int, callback: (UserBean) -> Unit = {}) {
        IdeaApi.getApiService(GetUserByIdService::class.java, false).getUser(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                Log.d(TAG, "getUserById 从服务器获取的UserBean信息 -> $it")
                setUserBeanInfo(it, state, "", callback)
            }))
    }

    /**
     * 从服务器获取该用户所有的好友信息
     * @param callback:在调用完成后做的事
     */
    private fun getAllFriendFromNet(callback: () -> Unit) {
        IdeaApi.getApiService(GetAllFriendService::class.java).getAllFriend()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.d(TAG, "getAllFriendFromNet 从服务器获取的所有好友信息 -> $it")
                if (it.isEmpty()) {
                    callback()
                } else {
                    for ((i, bean: UserBean) in it.withIndex()) {
                        //怕服务器返回null，所以加的判断
                        Log.d(TAG, "getAllFriendFromNet $i + ${it.size}")
                        setUserBeanInfo(bean, 4) { user ->
                            if (i == it.size - 1) {
                                Log.d(TAG, "getAllFriendFromNet 加载好友完毕")
                                callback()
                            }
                        }
                    }
                }

            }
            .doOnError {
                it.printStackTrace()
            }
            .subscribe()
    }

    /**
     * 保存用户个人信息更改
     * @param userBean:存放用户信息的UserBean
     * @param callback:正常操作中保存完毕后做的事
     * @param firstCallback:用户第一次登录中，保存完毕后做的事
     * @param token:在用户第一次登录时，该值不为empty，否则默认为empty
     */
    fun saveInfo(
        userBean: UserBean,
        callback: () -> Unit,
        token: String = "",
        firstCallback: () -> Unit = {}
    ) {
        saveInfoInDB(userBean)
        saveInfoInNet(userBean, callback, firstCallback, token)
    }

    /**
     * 将用户更改后的信息存入数据库
     * @param userBean:需要存入数据库的数据类
     * @param callback:存储完毕后
     */
    private fun saveInfoInDB(userBean: UserBean, callback: () -> Unit = {}) {
        val saveTemp = setFirstPinYin(userBean)
        userInfoDao.insertUserInfo(saveTemp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "saveInfoInDB 信息保存成功")
                callback()
            }
            .doOnError {
                Log.d(TAG, "saveInfoInDB 信息保存错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    /**
     * 将用户信息上传到服务器
     * @param userBean:需要上传到服务器的数据类
     * @param callback:上传完成后的回调
     * @param firstCallback:专用于第一次登录时上传完成的回调，在ViewModel进行下一步的处理
     * @param token:当为第一次登录时，本地未保存token，所有需要传值，当不是第一次登录时，为empty
     */
    private fun saveInfoInNet(
        userBean: UserBean,
        callback: () -> Unit = {},
        firstCallback: () -> Unit = {},
        token: String = ""
    ) {
        val service: UpdateUserInfoService = if (token.isEmpty()) {
            IdeaApi.getApiService(UpdateUserInfoService::class.java)
        } else {
            IdeaApi.getApiService(
                UpdateUserInfoService::class.java,
                false,
                OtherTokenInterceptor(token)
            )
        }
        service.update(userBeanToPostUserBean(userBean))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                if (it.success && token.isNotEmpty()) {
                    Log.d(TAG, "saveInfoInNet 用户信息上传成功 第一次登录")
                    firstCallback()
//                    doOnSaveSuccess(token, callback)
                } else {
                    Log.d(TAG, "saveInfoInNet 用户信息上传成功")
                    callback()
                }
            }))
    }

    /**
     * 将@link [UserBean] 转化为 @link [PostUserBean]
     * 方便上传给服务器
     */
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


    companion object {
        private const val TAG = "UserBeanRepository"

        @Volatile
        private var instance: UserBeanRepository? = null

        fun getInstance(userInfoDao: UserInfoDao) =
            instance ?: synchronized(this) {
                instance ?: UserBeanRepository(userInfoDao).also { instance = it }
            }
    }
}