package com.example.matechatting.mainprocess.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChangePasswordByTokenViewModelFactory(private val repository: ChangePasswordByTokenRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChangePasswordByTokenViewModel(repository) as T
    }
}