package com.example.matechatting.bean

import com.google.gson.annotations.SerializedName

data class IsLoginBean (
    @SerializedName("isOnline")
    var online: Boolean
)