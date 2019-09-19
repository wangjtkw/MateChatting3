package com.example.matechatting.mainprocess.direction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DirectionFragmentViewModelFactory(private val repository: DirectionFragmentRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DirectionFragmentViewModel(repository) as T
    }
}