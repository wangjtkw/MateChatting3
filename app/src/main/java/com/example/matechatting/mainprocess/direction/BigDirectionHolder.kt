package com.example.matechatting.mainprocess.direction

import android.os.Build
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.example.matechatting.R
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.base.BaseSource
import com.example.matechatting.bean.BigDirectionBean
import com.example.matechatting.databinding.ItemBigDirectionBinding

class BigDirectionHolder(private val binding: ItemBigDirectionBinding) : BaseHolder(binding) {

    override fun <T> bind(t: T) {
        if (t is BigDirectionSource) {
            binding.bigDirectionText.text = t.direction.directionName
            if (t.direction.isSelect) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.bigDirectionText.setTextColor(context.getColor(R.color.text_333333))
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.bigDirectionText.setTextColor(context.getColor(R.color.text_999797))
                }
            }
            if (t.direction.isSelectCurrent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.bigDirectionText.background = (context.getColor(R.color.bg_f6f6f6)).toDrawable()
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.bigDirectionText.background = (context.getColor(R.color.bg_ffffff)).toDrawable()
                }
            }
        }
    }

    fun getText(): TextView {
        return binding.bigDirectionText
    }
}

data class BigDirectionSource(
    val direction: BigDirectionBean
) : BaseSource()