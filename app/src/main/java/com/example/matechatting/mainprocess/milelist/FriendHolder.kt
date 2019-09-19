package com.example.matechatting.mainprocess.milelist

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.matechatting.MyApplication
import com.example.matechatting.R
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.bean.UserBean
import com.example.matechatting.databinding.ItemMileListFriendBinding

class FriendHolder(private val binding: ItemMileListFriendBinding) : BaseHolder(binding) {

    override fun <T> bind(t: T) {
        if (t is FriendSource) {
            val bean = t.friend
            binding.apply {
                Log.d("aaa", "isFirst ${bean.first}")
                if (bean.first) {
                    mileListTitle.visibility = View.VISIBLE
                    mileListTitle.text = bean.pinyin
                } else {
                    mileListTitle.visibility = View.GONE
                }
                if (bean.isLast) {
                    itemMileListFoot.visibility = View.VISIBLE
                } else {
                    itemMileListFoot.visibility = View.GONE
                }
                itemFriendName.text = bean.name
                itemFriendGraduate.text = bean.graduation
                itemFriendMajor.text = bean.direction
                itemFriendCompany.text = bean.company

                if (bean.onLine) {
                    mileListOnLineState.text = "在线"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mileListOnLineState.setTextColor(context.getColor(R.color.text_656bff))
                    }
                } else {
                    mileListOnLineState.text = "离线"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mileListOnLineState.setTextColor(context.getColor(R.color.text_8a8a8a))
                    }
                }
                if (!bean.headImage.isNullOrEmpty()) {
                    Glide.with(MyApplication.getContext())
                        .load(bean.headImage!!)
                        .into(itemFriendHead)
                }
            }
        }
    }

    fun getLayout(): LinearLayout {
        return binding.itemFriendLayout
    }
}

data class FriendSource(
    val friend: UserBean
)