package com.example.matechatting.mainprocess.direction

import androidx.recyclerview.widget.DiffUtil
import com.example.matechatting.R
import com.example.matechatting.base.BaseRecyclerAdapter
import com.example.matechatting.bean.SaveDirectionBean
import com.example.matechatting.databinding.ItemDirectionBinding

class SmallDirectionAdapter(private val bigDirectionId:Int,private val smallClickCallback: () -> Unit) :
    BaseRecyclerAdapter<ItemDirectionBinding, SaveDirectionBean.NormalDirection, SmallDirectionHolder, SmallDirectionSource>() {

    override fun initDiffCallback(): DiffUtil.ItemCallback<SaveDirectionBean.NormalDirection> {
        return object : DiffUtil.ItemCallback<SaveDirectionBean.NormalDirection>() {
            override fun areItemsTheSame(
                oldItem: SaveDirectionBean.NormalDirection,
                newItem: SaveDirectionBean.NormalDirection
            ): Boolean {
                return oldItem.smallDirection == newItem.smallDirection
            }

            override fun areContentsTheSame(
                oldItem: SaveDirectionBean.NormalDirection,
                newItem: SaveDirectionBean.NormalDirection
            ): Boolean {
                return oldItem.direction == newItem.direction
            }

        }
    }

    override fun freshData(list: List<SaveDirectionBean.NormalDirection>) {
        val array = ArrayList<SaveDirectionBean.NormalDirection>()
        array.addAll(mDiffer.currentList)
        array.addAll(list)
        mDiffer.submitList(array)
    }

    override fun onCreate(binding: ItemDirectionBinding): SmallDirectionHolder {
        return SmallDirectionHolder(bigDirectionId,binding)
    }

    override fun getItem(position: Int): SmallDirectionSource {
        return SmallDirectionSource(mDiffer.currentList[position],smallClickCallback)
    }

    override fun getLayoutId(): Int {
        return R.layout.item_direction
    }

    override fun onBind(holder: SmallDirectionHolder, position: Int) {
        holder.bind(getItem(position))
    }


}