package com.example.matechatting.mainprocess.chatting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.R
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.databinding.ItemChattingBinding

class ChattingAdapter : RecyclerView.Adapter<BaseHolder>() {
    private var array = ArrayList<ChattingBean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val binding = DataBindingUtil.inflate<ItemChattingBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_chatting, parent, false
        )
        return ChattingHolder(binding)
    }

    fun frashDatas(list: List<ChattingBean>){
        array.clear()
        array.addAll(list)
        notifyDataSetChanged()
    }

    fun frashData(bean: ChattingBean){
        array.add(bean)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    fun getCount():Int{
        return array.size
    }

    private fun getItem(position: Int): ChattingBean {
        return array[position]
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if (holder is ChattingHolder) {
            val bean = getItem(position)
            holder.bind(ChattingSource(bean))
        }
    }


}