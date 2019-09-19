package com.example.matechatting.mainprocess.chatting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.matechatting.R
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.databinding.ItemChattingBinding

class ChattingPagingAdapter : PagedListAdapter<ChattingBean, ChattingHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingHolder {
        val binding = DataBindingUtil.inflate<ItemChattingBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_chatting, parent, false
        )
        return ChattingHolder(binding)
    }

    override fun onBindViewHolder(holder: ChattingHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(ChattingSource(it))
        }

    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ChattingBean>() {
            override fun areItemsTheSame(oldItem: ChattingBean, newItem: ChattingBean): Boolean {
                return oldItem.message == newItem.message
            }

            override fun areContentsTheSame(oldItem: ChattingBean, newItem: ChattingBean): Boolean {
                return oldItem.time == newItem.time
            }

        }
    }
}