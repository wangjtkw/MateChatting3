package com.example.matechatting.mainprocess.album

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.matechatting.bean.AlbumListBean

class AlbumListRepository {

    fun getAlbumList(callback:(List<AlbumListBean>)){

    }

//    fun checkPermission(activity: Activity) {
//        if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//        } else {
//            getImage(activity)
//        }
//    }

    fun getImage(activity: Activity) {
        val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            arrayOf(
                MediaStore.Images.Media._ID
                , MediaStore.Images.Media.DATA
                , MediaStore.Images.Media.SIZE
                , MediaStore.Images.Media.DISPLAY_NAME
                , MediaStore.Images.Media.TITLE
                , MediaStore.Images.Media.WIDTH
                , MediaStore.Images.Media.HEIGHT
                , MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                , MediaStore.Images.Media.BUCKET_ID
            )
        } else {
            arrayOf(
                MediaStore.Images.Media._ID
                , MediaStore.Images.Media.DATA
                , MediaStore.Images.Media.SIZE
                , MediaStore.Images.Media.DISPLAY_NAME
                , MediaStore.Images.Media.TITLE
            )
        }

        val cursor = activity.contentResolver.query(
            mImageUri, projImage,
            MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
            arrayOf("image/jpeg", "image/png"),
            MediaStore.Images.Media.DATE_MODIFIED + " desc"
        ) ?: return
        while (cursor.moveToNext()) {
            val data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            val id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
            val size =
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)).toDouble() / (1024 * 1024).toDouble()
            val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
            val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
            val width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
            val height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))
//            , MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            val bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
            Log.d("AlbumImage", "filePath：$data")
            Log.d("AlbumImage", "id：$id")
            Log.d("AlbumImage", "size：$size")
            Log.d("AlbumImage", "displayName：$displayName")
            Log.d("AlbumImage", "title：$title")
            Log.d("AlbumImage", "width：$width")
            Log.d("AlbumImage", "height：$height")
            Log.d("AlbumImage", "bucketDisplayName：$bucketDisplayName")
            Log.d("AlbumImage", "end：")
        }
        cursor.close()
    }
}