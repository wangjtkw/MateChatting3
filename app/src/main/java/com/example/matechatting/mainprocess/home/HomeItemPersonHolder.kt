package com.example.matechatting.mainprocess.home

import android.widget.Button
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.matechatting.BASE_URL
import com.example.matechatting.MORE_BASE
import com.example.matechatting.PATH
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.base.BaseSource
import com.example.matechatting.bean.HomeItemBean
import com.example.matechatting.databinding.ItemHomePersonBinding

class HomeItemPersonHolder(private val binding: ItemHomePersonBinding) : BaseHolder(binding) {

    override fun <T> bind(t: T) {
        if (t is HomeItemSource) {
            val homeItemBean = t.homeItemBean
            binding.apply {
                itemPersonName.text = homeItemBean.name
                itemPersonGraduate.text = homeItemBean.graduation
                itemPersonMajor.text = homeItemBean.direction
                itemPersonCompany.text = homeItemBean.company
                if (!homeItemBean.headImage.isNullOrEmpty()) {
                    val sb = StringBuilder()
                    sb.append(BASE_URL)
                        .append(MORE_BASE)
                        .append(PATH)
                        .append(homeItemBean.headImage)
                    Glide.with(context).load(sb.toString()).into(itemPersonHead)
                }
            }
        }
    }

    fun getLayout(): LinearLayout {
        return binding.itemPersonLayout
    }

    fun getButton(): Button {
        return binding.itemPersonChangeButton
    }
}

data class HomeItemSource(
    val homeItemBean: HomeItemBean
) : BaseSource()