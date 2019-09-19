package com.example.matechatting.mainprocess.milelistsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matechatting.mainprocess.homesearch.HomeSearchRepository
import com.example.matechatting.mainprocess.homesearch.HomeSearchViewModel

class MileListSearchViewModelFactory(private val repository: MileListSearchRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MileListSearchViewModel(repository) as T
    }
}