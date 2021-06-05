package com.example.matechatting.utils

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.matechatting.BASE_URL
import com.example.matechatting.MORE_BASE
import com.example.matechatting.PATH
import com.example.matechatting.R


@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    val TAG = "bindImageFromUrl"
    Log.d(TAG,"imgUrl:$imageUrl")
    val sb = StringBuilder()
    sb.append(BASE_URL)
        .append(MORE_BASE)
        .append(PATH)
        .append(imageUrl)
    Glide.with(view.context)
        .load(sb.toString())
        .error(R.drawable.ic_head)
        .into(view)

//    if (!imageUrl.isNullOrEmpty()) {
//        Log.d("aaa","bindImageFromUrl $imageUrl")
//        val end = imageUrl.endsWith(".jpg")
//        if (end){
//            val bitmap = BitmapFactory.decodeFile(imageUrl)
//            view.setImageBitmap(bitmap)
//            Log.d("aaa","加载本地图片")
//        }else{
//
//        }
//
//
////        val imageLoader = ImageLoaderBuilder.getBuilder().build()
////        imageLoader.with(view.context).load(imageUrl,view)
//    }
}