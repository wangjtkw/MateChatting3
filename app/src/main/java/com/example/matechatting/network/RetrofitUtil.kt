package com.example.matechatting.network

import android.util.Log
import com.example.matechatting.MyApplication
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.UnsupportedEncodingException
import java.util.concurrent.TimeUnit

object RetrofitUtil {

    private fun getOkHttpClientBuilder(
        needToken: Boolean,
        tokenInterceptor: Interceptor? = null
    ): OkHttpClient.Builder {
        //超时时间
        val defaultTimeout = 10L
        //OkHttp日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor {
            try {
                Log.e("OkHttp------", it)
            } catch (e: UnsupportedEncodingException) {
                Log.e("OkHttp------", it)
            }
        }
        //日志等级
        // BASIC 请求/响应行
        // HEADER 请求/响应行 + 头
        // BODY 请求/响应行 + 头 + 体
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        //缓存设置
        val cacheFile = File(MyApplication.getContext().cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 100)//100MB
        val builder = OkHttpClient.Builder()
            .callTimeout(defaultTimeout, TimeUnit.SECONDS)
            .connectTimeout(defaultTimeout, TimeUnit.SECONDS)
            .readTimeout(defaultTimeout, TimeUnit.SECONDS)
            .writeTimeout(defaultTimeout, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .cache(cache)
        if (needToken) {
            builder.addInterceptor(TokenInterceptor())
        }
        if (tokenInterceptor != null) {
            Log.d("aaa", "拦截器添加成功")
            Log.d("aaa", tokenInterceptor.toString())
            builder.addInterceptor(tokenInterceptor)
        }
        return builder
    }

    fun getRetrofitBuilder(
        baseUrl: String,
        needToken: Boolean,
        tokenInterceptor: Interceptor? = null
    ): Retrofit.Builder {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create()

        val okHttpClient = getOkHttpClientBuilder(needToken, tokenInterceptor).build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
    }


}