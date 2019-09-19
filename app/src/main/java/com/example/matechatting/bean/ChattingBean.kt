package com.example.matechatting.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "chatting_news")
data class ChattingBean(
    //接收者id
    @ColumnInfo(name = "other_id")
    var otherId: Int = 0,
    //发送者id，考虑后期切换账号
    @ColumnInfo(name = "user_id")
    var userId: Int = 0,
    //信息的内容，为文字消息则为消息内容，否则为文件路径
    var message: String = "",
    //是否是文字消息
    var isString: Boolean = true,
    //发送消息的时间戳
    @PrimaryKey
    var time: String = "",
    //是否是发送的消息，区分消息左右
    var isUserSend: Boolean = true
) : Serializable