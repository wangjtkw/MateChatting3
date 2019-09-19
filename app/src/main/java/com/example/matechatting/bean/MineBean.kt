package com.example.matechatting.bean

import com.google.gson.annotations.SerializedName

data class MineBean(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("slogan")
    var slogan: String,
    @SerializedName("profile_photo")
    var headImage: String = ""

)

