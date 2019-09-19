package com.example.matechatting.mainprocess.repository

import android.util.Log
import com.example.matechatting.MyApplication
import com.example.matechatting.bean.AccountBean
import com.example.matechatting.database.AccountDao
import com.example.matechatting.mainprocess.login.LoginState
import com.example.matechatting.network.IdeaApi
import com.example.matechatting.network.LoginService
import com.example.matechatting.network.UpdateFirstService
import com.example.matechatting.utils.ExecuteObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AccountBeanRepository(private val accountDao: AccountDao) {

    /**
     * 从数据库获取AccountBean并保存到SharedPreferences中
     * @param token:通过token获取AccountBean信息
     * @param callback:信息存入完毕，进行下一步骤
     */
    fun saveAccountState(token: String, callback: () -> Unit) {
        accountDao.getAllByToken(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "saveAccountState 从数据库获取AccountBean成功 -> $it")
                saveState(it.account, it.token, it.id, it.inSchool)
                updateFirst(callback)
            }
            .doOnError {
                Log.d(TAG, "saveAccountState 从数据库获取AccountBean错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    /**
     * 将AccountBean账号信息保存到SharedPreferences中
     * @param account:用户的学号
     * @param token:用户的token
     * @param id:用户的ID
     * @param inSchool:是否已经毕业
     */
    private fun saveState(account: String, token: String, id: Int, inSchool: Boolean) {
        MyApplication.saveLoginState(account, token, id, inSchool)
    }

    /**
     * 该接口是与服务器端进行约定的一个接口
     * 用于更新用户的第一次登录状态
     */
    private fun updateFirst(callback: () -> Unit) {
        IdeaApi.getApiService(UpdateFirstService::class.java).updateFirst()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                if (it.success) {
                    Log.d(TAG, "updateFirst 第一次登陆状态更改成功 已改为非第一次登陆")
                    callback()
                }
            }))
    }

    /**
     * 从数据库检查用户账号密码输入是否正确
     * @param account:学号，账号
     * @param password:密码
     * @param callback:state 返回的状态码； list 因为返回参数未定，所以返回集合
     * 该函数实际目前未调用
     */
    fun checkFromDB(account: String, password: String, callback: (state: Int, List<String>) -> Unit) {
        accountDao.checkAccount(account)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                if (password == it.password) {
                    Log.d(TAG, "checkFromDB 从数据库验证账号成功 登陆")
                    saveState(account, it.token, it.id, it.inSchool)
                    callback(LoginState.NOT_FIRST, arrayListOf(""))
                } else if (password != it.password) {
                    Log.d(TAG, "checkFromDB 从数据库验证账号成功 账号或密码错误")
                    callback(LoginState.ERROR, arrayListOf(""))
                }
            }
            .doOnError {
                Log.d(TAG, "checkFromDB 从数据库验证账号错误（表示该用户未在本机登陆过）")
                callback(LoginState.NO_NETWORK, arrayListOf(""))
            }
            .subscribe()
    }

    /**
     * 通过服务器验证账号的正确性
     * @param account:用户的账号
     * @param password:用户的密码
     * @param callback:state: 返回的状态码；list:因为参数未定，所有返回集合
     */
    fun checkFromNet(
        account: String,
        password: String,
        callback: (state: Int, List<String>) -> Unit
    ) {
        IdeaApi.getApiService(LoginService::class.java, false).getLoginAndGetToken(account, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                Log.d(TAG, "checkFromNet 服务器返回的数据 -> $it")
                val payload = it.payload
                saveInDB(account, password, payload.token, payload.id, payload.inSchool)
                if (payload.first) {
                    Log.d(TAG, "checkFromNet 第一次登陆")
                    callback(LoginState.FIRST, arrayListOf(payload.token, payload.inSchool.toString()))
                } else {
                    Log.d(TAG, "checkFromNet 非第一次登陆")
                    saveState(account, payload.token, payload.id, payload.inSchool)
                    callback(LoginState.NOT_FIRST, arrayListOf(""))
                }
            }, onExecuteError = {
                Log.d(TAG, "checkFromNet 登陆验证错误")
                Log.d(TAG, it.message ?: "")
                callback(LoginState.ERROR, arrayListOf(""))
            }))
    }

    /**
     * 将账户信息保存到数据库中
     * @param account:账号（学号）
     * @param password:密码
     * @param token:该用户的token
     * @param id:该用户的ID
     * @param inSchool:该用户是否毕业
     * @param inSchool:true 未毕业 / false 毕业
     */
    private fun saveInDB(account: String, password: String, token: String, id: Int, inSchool: Boolean) {
        val accountBean = AccountBean(account, password, token, true, id, inSchool)
        accountDao.insertAccount(accountBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "saveInDB 账号信息保存数据库成功")
            }
            .doOnError {
                Log.d(TAG, "saveInDB 账号信息保存数据库错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }


    companion object {
        private const val TAG = "AccountBeanRepository"

        @Volatile
        private var instance: AccountBeanRepository? = null

        fun getInstance(accountDao: AccountDao) =
            instance ?: synchronized(this) {
                instance ?: AccountBeanRepository(accountDao).also { instance = it }
            }
    }
}