package com.example.matechatting.mainprocess.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.R
import com.example.matechatting.base.BaseRecyclerAdapter
import com.example.matechatting.bean.HomeItemBean
import com.example.matechatting.databinding.ItemHomePersonBinding
import com.example.matechatting.databinding.ItemMileListNewFriendBinding
import com.example.matechatting.mainprocess.milelist.NewFriendHolder

class HomeItemAdapter(
    var callbackPersonButton: (Int) -> Unit,
    var callbackPersonLayout: (Int) -> Unit
) :
    RecyclerView.Adapter<HomeItemPersonHolder>() {
    private val data = ArrayList<HomeItemBean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeItemPersonHolder {
        val binding = DataBindingUtil.inflate<ItemHomePersonBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_home_person, parent, false
        )
        return HomeItemPersonHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HomeItemPersonHolder, position: Int) {
        getItem(position).let { bean ->
            holder.bind(bean)
            holder.getLayout().setOnClickListener { callbackPersonLayout(bean.homeItemBean.id) }
            holder.getButton().setOnClickListener { callbackPersonButton(bean.homeItemBean.id) }
        }
    }

    fun freshData(list: List<HomeItemBean>) {
        val num = data.size
        data.addAll(list)
        notifyItemChanged(num, list.size)
    }

    private fun getItem(position: Int): HomeItemSource {
        return HomeItemSource(data[position])
    }

}