package com.example.matechatting.mainprocess.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.matechatting.R
import com.example.matechatting.bean.HomeItemBean
import com.example.matechatting.databinding.ItemHomePersonBinding

class HomeItemPagingAdapter(
    var callbackPersonButton: () -> Unit,
    var callbackPersonLayout: (Int) -> Unit
) :
    PagedListAdapter<HomeItemBean,HomeItemPersonHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeItemPersonHolder {
        val binding = DataBindingUtil.inflate<ItemHomePersonBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_home_person, parent, false
        )
        return HomeItemPersonHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeItemPersonHolder, position: Int) {
        getItem(position).let {bean->
            if (bean != null){
                holder.bind(HomeItemSource(bean))
                holder.getLayout().setOnClickListener { callbackPersonLayout(bean.id) }
                holder.getButton().setOnClickListener { callbackPersonButton() }
            }
        }
    }
    companion object{
        val diffCallback = object : DiffUtil.ItemCallback<HomeItemBean>() {
            override fun areItemsTheSame(oldItem: HomeItemBean, newItem: HomeItemBean): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: HomeItemBean, newItem: HomeItemBean): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}