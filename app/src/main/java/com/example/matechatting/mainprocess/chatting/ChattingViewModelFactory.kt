package com.example.matechatting.mainprocess.chatting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChattingViewModelFactory(private val repository: ChattingRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChattingViewModel(repository) as T
    }
}