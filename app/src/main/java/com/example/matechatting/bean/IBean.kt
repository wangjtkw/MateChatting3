package com.example.matechatting.bean

import com.google.gson.annotations.SerializedName

data class IBean(
    @SerializedName("page")
    var page: Int = 0
)