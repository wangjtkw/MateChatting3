package com.example.matechatting.mainprocess.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matechatting.mainprocess.repository.UserBeanRepository

class LoginViewModelFactory(
    private val repository: LoginRepository,
    private val userBeanRepository: UserBeanRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository, userBeanRepository) as T
    }
}