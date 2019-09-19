package com.example.matechatting.mainprocess.direction

import android.util.Log
import androidx.core.util.forEach
import androidx.core.util.isEmpty
import androidx.recyclerview.widget.DiffUtil
import com.example.matechatting.R
import com.example.matechatting.base.BaseRecyclerAdapter
import com.example.matechatting.bean.BigDirectionBean
import com.example.matechatting.databinding.ItemBigDirectionBinding

class BigDirectionAdapter(private val callback: (position: Int) -> Unit) :
    BaseRecyclerAdapter<ItemBigDirectionBinding, BigDirectionBean, BigDirectionHolder, BigDirectionSource>() {
    private var currentPosition = 0

    override fun initDiffCallback(): DiffUtil.ItemCallback<BigDirectionBean> {
        return object : DiffUtil.ItemCallback<BigDirectionBean>() {
            override fun areItemsTheSame(oldItem: BigDirectionBean, newItem: BigDirectionBean): Boolean {
                return oldItem.directionName == newItem.directionName
            }

            override fun areContentsTheSame(oldItem: BigDirectionBean, newItem: BigDirectionBean): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun freshData(list: List<BigDirectionBean>) {
        val arrayList = ArrayList<BigDirectionBean>()
        arrayList.addAll(mDiffer.currentList)
        arrayList.addAll(list)
        mDiffer.submitList(arrayList)
        mDiffer.currentList[0].isSelectCurrent = true
    }

    override fun onCreate(binding: ItemBigDirectionBinding): BigDirectionHolder {
        return BigDirectionHolder(binding)
    }

    override fun getItem(position: Int): BigDirectionSource {
        return BigDirectionSource(mDiffer.currentList[position])
    }

    override fun getLayoutId(): Int {
        return R.layout.item_big_direction
    }

    override fun onBind(holder: BigDirectionHolder, position: Int) {
        holder.bind(getItem(position))
        holder.getText().setOnClickListener {
            setItemChange(position)
            callback(position)
        }
    }

    fun setCurrentPosition(position: Int) {
        setItemChange(position)
    }

    fun setIsSelect() {
        mDiffer.currentList.forEach out@{
            DirectionNewActivity.saveMap.forEach { key, value ->
                if (it.id == value) {
                    it.isSelect = true
                    return@out
                } else {
                    it.isSelect = false
                }
            }
            if (DirectionNewActivity.saveMap.isEmpty()) {
                it.isSelect = false
            }
        }
        notifyDataSetChanged()
    }

    private fun setItemChange(position: Int) {
        if (position != currentPosition) {
            mDiffer.currentList[currentPosition].isSelectCurrent = false
            notifyItemChanged(currentPosition, 1)
            mDiffer.currentList[position].isSelectCurrent = true
            notifyItemChanged(position, 1)
            currentPosition = position
        }
    }

}