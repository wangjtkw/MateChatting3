package com.example.matechatting.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.matechatting.bean.ChattingBean
import io.reactivex.Single

@Dao
interface ChattingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChattingBean(chattingBean: ChattingBean): Single<Long>

    @Query("SELECT * FROM chatting_news WHERE user_id = :userId AND other_id = :otherId ORDER BY time DESC")
    fun getChattingBeanByIdPaging(otherId: Int, userId: Int): DataSource.Factory<Int, ChattingBean>

    @Query("SELECT * FROM chatting_news WHERE user_id = :userId AND other_id = :otherId ORDER BY time")
    fun getChattingBeanById(otherId: Int, userId: Int):Single<List<ChattingBean>>
}