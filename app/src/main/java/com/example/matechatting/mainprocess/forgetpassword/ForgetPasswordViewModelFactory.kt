package com.example.matechatting.mainprocess.forgetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ForgetPasswordViewModelFactory(private val repository: ForgetPasswordRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ForgetPasswordViewModel(repository) as T
    }
}