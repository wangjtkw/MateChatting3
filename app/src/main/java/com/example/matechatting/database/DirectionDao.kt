package com.example.matechatting.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.matechatting.bean.DirectionBean
import io.reactivex.Single

@Dao
interface DirectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDirection(directionBean: DirectionBean): Single<Long>

    @Query("UPDATE direction SET is_select = :isSelect WHERE id = :id")
    fun updateState(isSelect: Boolean, id: Int): Single<Int>

    @Query("SELECT * FROM direction WHERE parent_id = :parentId")
    fun selectDirectionByParent(parentId: Int): Single<List<DirectionBean>>

    @Query("SELECT * FROM direction WHERE directionName = :directionName")
    fun selectDirectionByName(directionName: String): Single<DirectionBean>

    @Query("SELECT * FROM direction WHERE id = :id")
    fun selectDirectionById(id: Int): Single<DirectionBean>

    @Query("UPDATE direction SET is_select = :state")
    fun clearSelectState(state: Boolean = false): Single<Int>
}