package com.example.matechatting.mainprocess.bindphone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BindPhoneViewModelFactory(private val repository: BindPhoneRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BindPhoneViewModel(repository) as T
    }
}