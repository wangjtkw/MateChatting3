package com.example.matechatting.bean

import com.google.gson.annotations.SerializedName

data class PostUserBean(
    @SerializedName("company")
    var company: String = "",
    @SerializedName("job")
    var job: String = "",
    @SerializedName("qq_account")
    var qqAccount: Long = 0L,
    @SerializedName("wechat_account")
    var wechatAccount: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("city")
    var city: String = "",
    @SerializedName("slogan")
    var slogan: String = ""
)