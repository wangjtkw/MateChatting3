package com.example.matechatting.mainprocess.album

import androidx.recyclerview.widget.DiffUtil
import com.example.matechatting.R
import com.example.matechatting.base.BaseRecyclerAdapter
import com.example.matechatting.databinding.ItemAlbumImageBinding


class AlbumRecyclerAdapter(private val callback: (url: String) -> Unit) :
    BaseRecyclerAdapter<ItemAlbumImageBinding, String, AlbumHolder, AlbumSource>() {

    override fun freshData(list: List<String>) {
        mDiffer.submitList(list)

    }

    override fun onCreate(binding: ItemAlbumImageBinding): AlbumHolder {
        return AlbumHolder(binding)
    }

    override fun getLayoutId(): Int {
        return R.layout.item_album_image
    }

    override fun getItem(position: Int): AlbumSource {
        return AlbumSource(mDiffer.currentList[position])
    }

    override fun initDiffCallback(): DiffUtil.ItemCallback<String> {
        return object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBind(holder: AlbumHolder, position: Int) {
        holder.bind(getItem(position))
        holder.getView().setOnClickListener {
            callback(mDiffer.currentList[position])
        }
    }
}