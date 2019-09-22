package com.example.matechatting.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.wifi.aware.WifiAwareManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


fun isNetworkConnected(context: Context): Int {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkState.NONE
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                val isAvailable = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                if (isAvailable) {
                    NetworkState.WIFI
                } else {
                    NetworkState.NONE
                }
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            -> NetworkState.MOBILE
            else -> NetworkState.NONE
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return NetworkState.NONE
        return when (networkInfo.type) {
            ConnectivityManager.TYPE_WIFI -> NetworkState.WIFI
            ConnectivityManager.TYPE_MOBILE_DUN -> NetworkState.MOBILE
            else -> NetworkState.NONE
        }
    }
}


class NetworkState {
    companion object {
        const val NONE = 0
        const val MOBILE = 1
        const val WIFI = 2
    }
}