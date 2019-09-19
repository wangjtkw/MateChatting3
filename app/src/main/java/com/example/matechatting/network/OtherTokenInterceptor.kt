package com.example.matechatting.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class OtherTokenInterceptor(private val token:String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (token.isEmpty()) {
            val originalRequest = chain.request()
            chain.proceed(originalRequest)
        } else {
            Log.d("aaa", "OtherTokenInterceptor : $token")
            val originalRequest = chain.request()
            val updateRequest = originalRequest.newBuilder()
                .header("token", token)
                .addHeader("Connection", "keep-alive").build()
            chain.proceed(updateRequest)
        }
    }

}