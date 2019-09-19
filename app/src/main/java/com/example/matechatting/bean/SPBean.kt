package com.example.matechatting.bean

import com.google.gson.annotations.SerializedName

data class SPBean(
    @SerializedName("payload")
    var payload: String,
    @SerializedName("success")
    var success: Boolean
)