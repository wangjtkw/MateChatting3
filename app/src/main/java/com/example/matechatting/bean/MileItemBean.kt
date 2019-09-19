package com.example.matechatting.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "mile_item")
data class MileItemBean(
    @SerializedName("company")
    var company: String = "",
    @SerializedName("directions")
    @Ignore
    val drec: List<String>? = null,
    @ColumnInfo(name = "graduation_year")
    @SerializedName("graduation_year")
    var graduationYear: Int = 0,
    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @ColumnInfo(name = "stu_id")
    @SerializedName("stu_id")
    var stuId: String = "",
    @ColumnInfo(name = "profile_photo")
    @SerializedName("profile_photo")
    var headImage: String = ""
) {
    @ColumnInfo(name = "direction")
    var direction: String = ""
    @ColumnInfo(name = "graduation")
    var graduation: String = ""

//
//    constructor():this("",null,0,0,"","","")
}