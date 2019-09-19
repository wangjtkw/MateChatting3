package com.example.matechatting.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerScrollListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val mLastChildPosition = layoutManager.findLastVisibleItemPosition()//当前页面最后一个可见的item的位置
        val itemTotalCount = layoutManager.itemCount//获取总的item的数量
        if (!isLastPage() && mLastChildPosition == itemTotalCount - 5) {//当前页面的最后一个item是item全部的最后一个并且当前页面的最后一个item的底部是recycleView的底部的时候，获取新数据
            if (!isLoading) {
                isLoading = true
                loadMoreItems {
                    isLoading = it
                }
            }
        }
    }

    abstract fun isLastPage(): Boolean

    abstract fun loadMoreItems(callback: (Boolean) -> Unit)

    companion object {
        private var isLoading = false
    }
}