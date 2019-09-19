package com.example.matechatting.mainprocess.chatting

import androidx.recyclerview.widget.DiffUtil
import com.example.matechatting.R
import com.example.matechatting.base.BaseRecyclerAdapter
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.databinding.ItemChattingBinding

class ChattingDifferAdapter : BaseRecyclerAdapter<ItemChattingBinding, ChattingBean, ChattingHolder, ChattingSource>() {
    override fun initDiffCallback(): DiffUtil.ItemCallback<ChattingBean> {
        return object : DiffUtil.ItemCallback<ChattingBean>() {
            override fun areItemsTheSame(oldItem: ChattingBean, newItem: ChattingBean): Boolean {
                return oldItem.time == newItem.time
            }

            override fun areContentsTheSame(oldItem: ChattingBean, newItem: ChattingBean): Boolean {
                return oldItem.message == newItem.message
            }
        }
    }


    override fun freshData(list: List<ChattingBean>) {
        val arrayList = ArrayList<ChattingBean>()
        arrayList.addAll(list)
        mDiffer.submitList(arrayList)
    }

    override fun onCreate(binding: ItemChattingBinding): ChattingHolder {
        return ChattingHolder(binding)
    }

    override fun getItem(position: Int): ChattingSource {
        return ChattingSource(mDiffer.currentList[position])
    }

    override fun getLayoutId(): Int {
        return R.layout.item_chatting
    }

    override fun onBind(holder: ChattingHolder, position: Int) {
        val bean = getItem(position)
        holder.bind(bean)
    }

}