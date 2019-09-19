package com.example.matechatting.mainprocess.chatting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.matechatting.bean.ChattingBean

class ChattingViewModel(private val repository: ChattingRepository) : ViewModel() {
    var newsList = MutableLiveData<List<ChattingBean>>()
    private val tempArray = ArrayList<ChattingBean>()

    fun getDataPaging(otherId: Int, userId: Int, callback: (LiveData<PagedList<ChattingBean>>) -> Unit) {
        repository.getChattingFromDBPaging(otherId, userId, callback)
    }

    fun getData(otherId: Int, userId: Int) {
        repository.getChatting(otherId, userId) {
            tempArray.addAll(it)
            newsList.value = tempArray
        }
    }

    fun insertMessage(chattingBean: ChattingBean) {
        repository.insertMessage(chattingBean) {
            tempArray.add(chattingBean)
            newsList.value = tempArray
        }
    }

    fun updateState(state: Int, otherId: Int, callback: () -> Unit = {}) {
        repository.changeState(state, otherId, callback)
    }
}