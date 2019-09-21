package com.example.matechatting.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.matechatting.bean.HasMessageBean
import io.reactivex.Single

@Dao
interface HasMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(hasMessageBean: HasMessageBean): Single<Long>

    @Query("SELECT otherId FROM has_message WHERE hasMessage = :hasMessage")
    fun getHasMessage(hasMessage: Boolean): Single<List<Int>>

    @Query("DELETE FROM has_message WHERE userId = :userId AND otherId = :otherId")
    fun deleteHasMessage(userId: Int, otherId: Int):Single<Int>
}