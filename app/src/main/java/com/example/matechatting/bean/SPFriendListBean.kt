package com.example.matechatting.bean

import com.google.gson.annotations.SerializedName

data class SPFriendListBean(
    @SerializedName("payload")
    var payload: List<Int>,
    @SerializedName("success")
    var success: Boolean
)