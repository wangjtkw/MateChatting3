package com.example.matechatting.mainprocess.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MineViewModelFactory(private val repository: MineRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MineViewModel(repository) as T
    }
}