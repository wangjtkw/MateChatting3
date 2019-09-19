package com.example.matechatting.mainprocess.homesearch

import android.widget.FrameLayout
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.databinding.ItemSearchResultFootBinding

class HomeSearchResultFootHolder(private val binding: ItemSearchResultFootBinding) : BaseHolder(binding) {
    fun getView(): FrameLayout {
        return binding.resultFootLayout


    }
}