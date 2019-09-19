package com.example.matechatting.mainprocess.homesearch

import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.matechatting.BASE_URL
import com.example.matechatting.MORE_BASE
import com.example.matechatting.PATH
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.bean.SearchBean
import com.example.matechatting.databinding.ItemSearchResultBinding

class HomeSearchResultHolder(private val binding: ItemSearchResultBinding) : BaseHolder(binding) {

    override fun <T> bind(t: T) {
        if (t is HomeSearchResultSource) {
            val searchBean = t.data
            binding.apply {
                itemSearchName.text = searchBean.name
                itemSearchGraduate.text = searchBean.graduation
                itemSearchMajor.text = searchBean.direction
                itemSearchCompany.text = searchBean.company
                if (!searchBean.headImage.isNullOrEmpty()) {
                    val sb = StringBuilder()
                    sb.append(BASE_URL)
                        .append(MORE_BASE)
                        .append(PATH)
                        .append(searchBean.headImage)
                    Log.d("aaa","头像 $sb")
                    Glide.with(context).load(sb.toString()).into(itemSearchHead)
                }
            }
        }
    }

    fun getLayout(): LinearLayout {
        return binding.itemSearchLayout
    }

    fun getButton(): Button {
        return binding.itemSearchChangeButton
    }

}

data class HomeSearchResultSource(
    val data: SearchBean.Payload.MyArray.Map
)