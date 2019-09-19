package com.example.matechatting.utils

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.matechatting.R


@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Log.d("aaa","bindImageFromUrl $imageUrl")
        val end = imageUrl.endsWith(".jpg")
        if (end){
            val bitmap = BitmapFactory.decodeFile(imageUrl)
            view.setImageBitmap(bitmap)
            Log.d("aaa","加载本地图片")
        }else{
            Glide.with(view.context)
                .load(imageUrl)
                .error(R.drawable.ic_head)
                .into(view)
        }


//        val imageLoader = ImageLoaderBuilder.getBuilder().build()
//        imageLoader.with(view.context).load(imageUrl,view)
    }
}