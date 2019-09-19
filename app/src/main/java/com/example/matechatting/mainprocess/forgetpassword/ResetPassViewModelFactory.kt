package com.example.matechatting.mainprocess.forgetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ResetPassViewModelFactory(private val repository: ResetPassRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ResetPassViewModel(repository) as T
    }
}