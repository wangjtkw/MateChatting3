package com.example.matechatting.mainprocess.cliphead

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClipViewModelFactory(private val repository: ClipRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClipViewModel(repository) as T
    }
}