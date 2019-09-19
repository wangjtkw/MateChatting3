package com.example.matechatting.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<B : ViewDataBinding, D : Any, HD : BaseHolder, S : BaseSource> :
    RecyclerView.Adapter<HD>() {
    protected var mDiffer = AsyncListDiffer(this, initDiffCallback())
    protected lateinit var binding: B
    private var isInit = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HD {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            getLayoutId(), parent, false
        )
//        initAsyncListDiffer()
        return onCreate(binding)
    }

    override fun onBindViewHolder(holder: HD, position: Int) {
        onBind(holder, position)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size

    }

//    private fun initAsyncListDiffer() {
//        mDiffer = AsyncListDiffer(this, initDiffCallback())
//        isInit = true
//    }

    abstract fun initDiffCallback(): DiffUtil.ItemCallback<D>
    abstract fun freshData(list: List<D>)
    abstract fun onCreate(binding: B): HD
    abstract fun getItem(position: Int): S
    abstract fun getLayoutId(): Int
    abstract fun onBind(holder: HD, position: Int)

}