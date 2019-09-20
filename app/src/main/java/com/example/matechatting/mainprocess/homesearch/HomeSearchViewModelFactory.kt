package com.example.matechatting.mainprocess.homesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matechatting.mainprocess.repository.UserBeanRepository

class HomeSearchViewModelFactory(
    private val repository: HomeSearchRepository,
    private val userBeanRepository: UserBeanRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeSearchViewModel(repository, userBeanRepository) as T
    }
}