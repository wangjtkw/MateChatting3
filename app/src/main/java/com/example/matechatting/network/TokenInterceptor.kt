package com.example.matechatting.network

import android.util.Log
import com.example.matechatting.MyApplication
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = MyApplication.getToken()
        Log.d("aaa","token $token")
        return if (token.isNullOrEmpty()) {
            val originalRequest = chain.request()
            chain.proceed(originalRequest)
        } else {
            val originalRequest = chain.request()
            val updateRequest =
                originalRequest.newBuilder()
                    .header("token", token)
                    .addHeader("Connection", "keep-alive")
                    .build()
            chain.proceed(updateRequest)
        }
    }

}