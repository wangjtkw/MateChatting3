package com.example.matechatting.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat


class AlbumImage {
//    private val mGroupMap = HashMap<String, ArrayList<String>>()
//    private val list = ArrayList<ImageBean>()

    private val TAG = "AlbumImage"
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(activity: Activity, callback: (list: List<String>) -> Unit) {
        if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getAllImage(activity, callback)
        }
    }

    fun getAllImage(activity: Activity, callback: (list: List<String>) -> Unit) {
        val arrayList = ArrayList<String>()
        val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = activity.contentResolver.query(
            mImageUri, null,
            MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
            arrayOf("image/jpeg", "image/png"),
            MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        ) ?: return
//        while (cursor.moveToNext()) {
//            //图片路径
//            val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
//
//            File(path).parentFile?.name?.let {
//                if (!mGroupMap.containsKey(it)) {
//                    val chileList = ArrayList<String>()
//                    chileList.add(path)
//                    mGroupMap.put(it, chileList)
//                } else {
//                    mGroupMap[it]?.add(path)
//                }
//            }
//        }
        while (cursor.moveToNext()) {
            val data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
//            val id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
//            val size =
//                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)).toDouble() / (1024 * 1024).toDouble()
//            val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
//            val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
//            val width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
//            val height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))
            arrayList.add(data)
//            Log.d("AlbumImage", "filePath：$data")
//            Log.d("AlbumImage", "id：$id")
//            Log.d("AlbumImage", "size：$size")
//            Log.d("AlbumImage", "displayName：$displayName")
//            Log.d("AlbumImage", "title：$title")
//            Log.d("AlbumImage", "width：$width")
//            Log.d("AlbumImage", "height：$height")
//            Log.d("AlbumImage", "end：")
        }
        callback(arrayList)
        cursor.close()
    }
}