package com.example.matechatting.mainprocess.myinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyInfoViewModelFactory(private val repository: MyInfoRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyInfoViewModel(repository) as T
    }
}