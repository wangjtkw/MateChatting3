package com.example.matechatting.mainprocess.album
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.base.BaseSource
import com.example.matechatting.databinding.ItemAlbumImageBinding

class AlbumHolder(private val binding: ItemAlbumImageBinding) : BaseHolder(binding) {
    override fun <T> bind(t: T) {
        if (t is AlbumSource){
            Glide.with(context).load(t.url).into(binding.image)
        }
    }

    fun getView():ImageView{
        return binding.image
    }
}

data class AlbumSource(val url: String): BaseSource()
