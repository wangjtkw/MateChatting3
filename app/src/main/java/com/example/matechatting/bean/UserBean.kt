package com.example.matechatting.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_info")
data class UserBean(
    @SerializedName("city")
    var city: String = "",
    @SerializedName("company")
    var company: String = "",
    @SerializedName("directions")
    @Ignore
    var directions: List<String>? = null,
    @SerializedName("email")
    var email: String = "",
    @SerializedName("graduation_year")
    var graduationYear: Int = 0,
    @SerializedName("job")
    var job: String = "",
    @SerializedName("major_name")
    @ColumnInfo(name = "major_name")
    var majorName: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("phone_number")
    @ColumnInfo(name = "phone_number")
    var phoneNumber: Long = 0L,
    @SerializedName("qq_account")
    @ColumnInfo(name = "qq_account")
    var qqAccount: Long = 0,
    @SerializedName("slogan")
    var slogan: String = "",
    @SerializedName("stu_id")
    @ColumnInfo(name = "stu_id")
    var stuId: String = "",
    @SerializedName("wechat_account")
    @ColumnInfo(name = "wechat_account")
    var wechatAccount: String = "",
    @ColumnInfo(name = "profile_photo")
    @SerializedName("profile_photo")
    var headImage: String? = "",
    @ColumnInfo(name = "is_man")
    @SerializedName("is_man")
    var man: Boolean = true,
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "in_school")
    @SerializedName("in_school")
    var inSchool: Boolean = false
) {
    @ColumnInfo(name = "direction")
    var direction: String = ""
    @ColumnInfo(name = "graduation")
    var graduation: String = ""

    var first: Boolean = false

    /**
     * state:0(陌生人)，1（自己），2（新好友），3（聊天好友），4（好友）
     */
    var state: Int = 0
    @ColumnInfo(name = "on_line")
    var onLine: Boolean = false
    var pinyin: String = ""
    @Ignore
    var isLast = false
}