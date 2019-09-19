package com.example.matechatting.mainprocess.milelist

import androidx.lifecycle.ViewModel
import com.example.matechatting.bean.UserBean

class MileListViewModel(private val repository: MileListRepository) : ViewModel() {

    fun getAllNewFriend(callback: (List<UserBean>) -> Unit) {
        repository.getUserByState(2, callback)
    }

    fun getAllNewChatting(callback: (List<UserBean>) -> Unit) {
        repository.getUserByState(3, callback)
    }

    fun getAllFriend(callback: (List<UserBean>) -> Unit) {
        repository.getAllFriendFromDB(callback)
    }

    fun updateState(userBean: UserBean, state: Int, callback: () -> Unit) {
        repository.updateState(userBean, state, callback)
    }

    fun removeFriend(userBean: UserBean, callback: () -> Unit) {
        repository.removeFriend(userBean, callback)
    }

}