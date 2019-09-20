package com.example.matechatting.mainprocess.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matechatting.mainprocess.repository.UserBeanRepository

class HomeItemViewModelFactory(private val repository: HomeItemRepository,private val userBeanRepository: UserBeanRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeItemViewModel(repository,userBeanRepository) as T
    }
}