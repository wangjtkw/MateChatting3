package com.example.matechatting.bean

import androidx.room.Entity

@Entity(tableName = "has_message",primaryKeys = ["otherId","userId"])
data class HasMessageBean(
    val otherId: Int,
    val userId: Int,
    val hasMessage: Boolean
)