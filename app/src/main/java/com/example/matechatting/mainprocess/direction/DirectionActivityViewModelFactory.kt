package com.example.matechatting.mainprocess.direction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DirectionActivityViewModelFactory(private val repository: DirectionActivityRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DirectionActivityViewModel(repository) as T
    }
}