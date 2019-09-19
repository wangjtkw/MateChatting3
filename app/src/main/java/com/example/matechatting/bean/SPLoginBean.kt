package com.example.matechatting.bean

import com.google.gson.annotations.SerializedName

data class SPLoginBean(
    @SerializedName("payload")
    var payload: Payload,
    @SerializedName("success")
    var success: Boolean
) {
    data class Payload(
        @SerializedName("first")
        var first: Boolean,
        @SerializedName("token")
        var token: String,
        @SerializedName("in_school")
        var inSchool:Boolean,
        @SerializedName("id")
        var id:Int
    )
}