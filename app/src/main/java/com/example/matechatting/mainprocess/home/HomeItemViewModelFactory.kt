package com.example.matechatting.mainprocess.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeItemViewModelFactory(private val repository: HomeItemRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeItemViewModel(repository) as T
    }
}