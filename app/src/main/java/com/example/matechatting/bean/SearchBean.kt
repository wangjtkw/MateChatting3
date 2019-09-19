package com.example.matechatting.bean

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName


data class SearchBean(
    @SerializedName("payload")
    var payload: Payload,
    @SerializedName("success")
    var success: Boolean
) {
    data class Payload(
        @SerializedName("empty")
        var empty: Boolean,
        @SerializedName("myArrayList")
        var myArrayList: List<MyArray>
    ) {
        data class MyArray(
            @SerializedName("empty")
            var empty: Boolean,
            @SerializedName("map")
            var map: Map
        ) {
            data class Map(
                @SerializedName("city")
                var city: String,
                @SerializedName("company")
                var company: String,
                @SerializedName("directions")
                var directions: Directions,
                @SerializedName("email")
                var email: String,
                @SerializedName("is_man")
                var gender: String,
                @SerializedName("graduation_year")
                var graduationYear: Int,
                @SerializedName("id")
                var id: Int,
                @SerializedName("is_deleted")
                var isDeleted: Boolean,
                @SerializedName("job")
                var job: String,
                @SerializedName("major_name")
                var majorName: String,
                @SerializedName("name")
                var name: String,
                @SerializedName("qq_account")
                var qqAccount: Int,
                @SerializedName("slogan")
                var slogan: String,
                @SerializedName("stu_id")
                var stuId: String,
                @SerializedName("wechat_account")
                var wechatAccount: String,
                @ColumnInfo(name = "profile_photo")
                @SerializedName("profile_photo")
                var headImage: String = ""
            ) {
                var direction = ""
                var graduation = ""

                data class Directions(
                    @SerializedName("empty")
                    var empty: Boolean,
                    @SerializedName("myArrayList")
                    var myArrayList: List<String>
                )
            }
        }
    }
}