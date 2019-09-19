package com.example.matechatting.database

import androidx.room.*
import com.example.matechatting.bean.AccountBean
import io.reactivex.Single

@Dao
interface AccountDao{

    @Query("SELECT * FROM account WHERE account = :account")
    fun checkAccount(account:String): Single<AccountBean>

    @Query("SELECT token FROM account WHERE account = :account")
    fun getToken(account: String):Single<String>

    @Query("SELECT * FROM account WHERE login = :login")
    fun getLoginToken(login:Boolean):Single<AccountBean>

    @Query("SELECT * FROM account WHERE token = :token")
    fun getAllByToken(token:String):Single<AccountBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: AccountBean):Single<Long>

    @Delete
    fun deleteAccount(account: AccountBean)

    @Query("DELETE FROM account")
    fun deleteAll()

    @Query("UPDATE account SET password = :password WHERE account = :account")
    fun updateAccount(account: String, password:String)
}