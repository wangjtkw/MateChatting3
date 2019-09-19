package com.example.matechatting.base

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.isNetworkConnected

abstract class BaseFragment : Fragment() {

    protected fun init() {
        initView()
        Log.d("aaa", "$isLogin")
        if (isLogin) {
            initLogin()
        } else {
            initNotLogin()
        }
    }

    abstract fun initView()

    open fun initLogin(){}

    open fun initNotLogin(){}

    /**
     * 监听网络状态
     */
    protected fun listenNetwork() {
        val connectivityManager = ContextCompat.getSystemService(requireContext(), ConnectivityManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.apply {
                registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        when (isNetworkConnected(requireContext())) {
                            NetworkState.WIFI -> {
                                doOnWifi();doOnNetworkAvailable()
                            }
                            NetworkState.MOBILE -> {
                                doOnMobile();doOnNetworkAvailable()
                            }
                            else -> {
                                doOnNetworkNotAvailable()
                            }
                        }
                    }
                })
            }
        }
    }

    open fun doOnWifi() {}

    open fun doOnMobile() {}

    open fun doOnNetworkAvailable() {}

    open fun doOnNetworkNotAvailable() {}

    companion object {
        var isLogin = false
        var account = ""
        var userId = 0
    }
}