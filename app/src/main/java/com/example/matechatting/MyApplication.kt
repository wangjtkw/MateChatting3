package com.example.matechatting

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.multidex.MultiDexApplication
import com.squareup.leakcanary.RefWatcher
import io.reactivex.plugins.RxJavaPlugins


class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return
//        }
        RxJavaPlugins.setErrorHandler {
            val message = it.message ?: ""
            Log.d("aaa", message)
        }
//        sRefWatcher = LeakCanary.install(this)
        context = applicationContext
    }

    companion object {
        private lateinit var context: Context

        private var sRefWatcher: RefWatcher? = null

        fun getContext(): Context {
            return context
        }

        fun saveLoginState(account: String, token: String, id: Int, inSchool: Boolean) {
            context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE).edit {
                putBoolean("isLogin", true)
                putString("account", account)
                putString("token", token)
                putInt("userId", id)
                putBoolean("inSchool", inSchool)
                commit()
            }
        }

        fun getToken(): String? {
            val sharedPreferences = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
            return sharedPreferences.getString("token", "")
        }

        fun getInSchool(): Boolean? {
            val sharedPreferences = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("inSchool", false)
        }

        fun getUserId(): Int? {
            val sharedPreferences = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
            return sharedPreferences.getInt("userId", 0)
        }
    }

}