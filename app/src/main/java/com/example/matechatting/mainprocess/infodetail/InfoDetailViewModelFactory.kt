package com.example.matechatting.mainprocess.infodetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matechatting.mainprocess.repository.UserBeanRepository

class InfoDetailViewModelFactory(private val repository: UserBeanRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InfoDetailViewModel(repository) as T
    }
}