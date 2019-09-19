package com.example.matechatting.mainprocess.milelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MileListViewModelFactory(private val repository: MileListRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MileListViewModel(repository) as T
    }
}