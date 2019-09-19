package com.example.matechatting.network

import com.example.matechatting.BASE_URL
import okhttp3.Interceptor


object IdeaApi {
    fun <T> getApiService(
        cls: Class<T>,
        needToken: Boolean = true,
        tokenInterceptor: Interceptor? = null,
        baseUrl: String = BASE_URL
    ): T {
        val retrofit = RetrofitUtil.getRetrofitBuilder(baseUrl, needToken, tokenInterceptor).build()
        return retrofit.create(cls)
    }

}