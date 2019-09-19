package com.example.matechatting.mainprocess.homesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeSearchViewModelFactory(private val repository: HomeSearchRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeSearchViewModel(repository) as T
    }
}