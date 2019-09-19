package com.example.matechatting.mainprocess.homesearch

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.R
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.bean.SearchBean
import com.example.matechatting.databinding.ItemSearchResultBinding
import com.example.matechatting.databinding.ItemSearchResultFootBinding

class HomeSearchResultAdapter(
    var callbackPersonButton: (Int) -> Unit,
    var callbackPersonLayout: (Int) -> Unit,
    var callbackLoadMore: (Int) -> Unit
) : RecyclerView.Adapter<BaseHolder>() {
    private var dataList = ArrayList<SearchBean.Payload.MyArray.Map>()
    private var page = 1
    var needGone = false

    fun frashData(list: List<SearchBean.Payload.MyArray.Map>) {
        val old = dataList.size
        dataList.addAll(list)
        notifyItemRangeChanged(old, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        when (viewType) {
            FOOT -> {
                val binding = DataBindingUtil.inflate<ItemSearchResultFootBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_search_result_foot, parent, false
                )
                return HomeSearchResultFootHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemSearchResultBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_search_result, parent, false
                )
                return HomeSearchResultHolder(binding)
            }
        }
    }


    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        when (holder) {
            is HomeSearchResultHolder -> {
                Log.d("aaa","HomeItemPersonHolder")
                holder.bind(HomeSearchResultSource(getItem(position)))
                holder.getLayout().setOnClickListener { callbackPersonLayout(getItem(position).id) }
                holder.getButton().setOnClickListener { callbackPersonButton(getItem(position).id) }
            }
            is HomeSearchResultFootHolder -> {
                Log.d("aaa","HomeSearchResultFootHolder")
                if (needGone) {
                    holder.getView().visibility = View.GONE
                } else {
                    holder.getView().setOnClickListener { callbackLoadMore(getPage()) }
                }
            }
        }
    }

    private fun getPage(): Int {
        return ++page
    }


    private fun getItem(position: Int): SearchBean.Payload.MyArray.Map {
        return dataList[position]
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("aaa","position $position  dataList.size ${dataList.size}" )
        return when (position) {
            dataList.size -> FOOT
            else -> VIEW
        }
    }

    override fun getItemCount(): Int {
        return dataList.size + 1
    }

    fun setFootGone() {

    }

    companion object {
        private const val VIEW = 0
        private const val FOOT = 1
    }
}