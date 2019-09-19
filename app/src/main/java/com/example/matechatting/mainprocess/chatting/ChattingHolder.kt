package com.example.matechatting.mainprocess.chatting

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.matechatting.MyApplication
import com.example.matechatting.R
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.base.BaseSource
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.database.AppDatabase
import com.example.matechatting.databinding.ItemChattingBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChattingHolder(private val binding: ItemChattingBinding) : BaseHolder(binding) {
    override fun <T> bind(t: T) {
        if (t is ChattingSource) {
            val bean = t.chattingBean
            binding.chattingTimeText.text = bean.time
            if (bean.isUserSend) {
                initUser(bean)
            } else {
                initOther(bean)
            }
        }
    }

    private fun initUser(bean: ChattingBean) {
        binding.chattingOtherGroup.visibility = View.GONE
        binding.chattingUserGroup.visibility = View.VISIBLE
        binding.chattingUserMessageText.text = bean.message
        getHeadImage(MyApplication.getUserId()!!,binding.chattingUserHead)

    }

    private fun initOther(bean: ChattingBean) {
        binding.chattingOtherGroup.visibility = View.VISIBLE
        binding.chattingUserGroup.visibility = View.GONE
        binding.chattingOtherMessageText.text = bean.message
        getHeadImage(ChattingActivity.id,binding.chattingOtherHead)
    }

    private fun getHeadImage(id:Int,view: ImageView){
        AppDatabase.getInstance(MyApplication.getContext()).userInfoDao().getHeadImage(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                setHeadImage(it,view)
            }
            .doOnError {

            }
            .subscribe()
    }

    private fun setHeadImage(url:String,view:ImageView){
        Glide.with(context)
            .load(url)
            .error(R.drawable.ic_head)
            .into(view)
    }
}

data class ChattingSource(
    val chattingBean: ChattingBean
) : BaseSource()