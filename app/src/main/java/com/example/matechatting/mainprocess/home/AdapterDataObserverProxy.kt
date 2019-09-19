package com.example.matechatting.mainprocess.home

import androidx.recyclerview.widget.RecyclerView

class AdapterDataObserverProxy(
    private val adapterDataObserver: RecyclerView.AdapterDataObserver,
    private val headCount: Int
) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        adapterDataObserver.onChanged()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        adapterDataObserver.onItemRangeRemoved(positionStart + headCount, itemCount)
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        adapterDataObserver.onItemRangeMoved(fromPosition + headCount, toPosition + headCount, itemCount)
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        adapterDataObserver.onItemRangeInserted(positionStart + headCount, itemCount)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        adapterDataObserver.onItemRangeChanged(positionStart + headCount, itemCount)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        adapterDataObserver.onItemRangeChanged(positionStart + headCount, itemCount, payload)
    }
}