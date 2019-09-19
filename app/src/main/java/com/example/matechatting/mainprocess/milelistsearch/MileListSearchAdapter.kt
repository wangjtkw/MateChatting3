package com.example.matechatting.mainprocess.milelistsearch

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.R
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.bean.UserBean
import com.example.matechatting.databinding.ItemMileListFriendBinding
import com.example.matechatting.mainprocess.milelist.FriendHolder
import com.example.matechatting.mainprocess.milelist.FriendSource

class MileListSearchAdapter(private val friendLayoutCallback: (Int) -> Unit) : RecyclerView.Adapter<BaseHolder>() {
    private var friendList = ArrayList<UserBean>()

    fun freshData(list: List<UserBean>) {
        friendList.clear()
        friendList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val binding = DataBindingUtil.inflate<ItemMileListFriendBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_mile_list_friend, parent, false
        )
        return FriendHolder(binding)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    private fun getFriendItem(position: Int): UserBean {
        return friendList[position]
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        Log.d("aaa", "FriendHolder")
        if (holder is FriendHolder) {
            val bean = getFriendItem(position)
            holder.bind(FriendSource(bean))
            holder.getLayout().setOnClickListener { friendLayoutCallback(bean.id) }
        }
    }

}