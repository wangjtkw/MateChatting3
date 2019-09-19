package com.example.matechatting.mainprocess.cliphead

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.matechatting.MyApplication
import com.example.matechatting.R
import com.example.matechatting.mainprocess.mine.MineFragment
import com.example.matechatting.myview.CropView
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.dialog.MessageTitleOkDialogUtil
import com.example.matechatting.utils.isNetworkConnected
import com.example.matechatting.utils.statusbar.StatusBarUtil
import java.io.File
import java.io.IOException
import java.io.OutputStream

class ClipActivity : AppCompatActivity() {
    private lateinit var back: FrameLayout
    private lateinit var over: TextView
    private lateinit var clipView: CropView
    private lateinit var viewModel: ClipViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clip)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setStatusBarDarkTheme(this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, this.getColor(R.color.bg_ffffff))
        }
        initView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            init()
        }
    }

    private fun initView() {
        val factory = InjectorUtils.provideClipViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(ClipViewModel::class.java)
        back = findViewById(R.id.clip_back)
        over = findViewById(R.id.clip_text_over)
        clipView = findViewById(R.id.crop_view)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun init() {
        val path = intent.getStringExtra("image_uri")
        if (path.isNullOrEmpty()) {
            return
        }
        val point = Point()
        this.windowManager.defaultDisplay.getSize(point)
        val width = point.x * (7 / 9f)
        clipView.apply {
            setBitmapForHeight(path, 1500)
            radius = width
            maxScale = 3f
            doubleClickScale = 1f
        }

        over.setOnClickListener {
            whenBack()
        }
        back.setOnClickListener {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun generateUriAndReturn() {
        val zoomedCropBitmap = clipView.clip() ?: return
        val file = File(MyApplication.getContext().cacheDir, "cropped_" + System.currentTimeMillis() + ".jpg")
        Log.d("aaa","file path ${file.absolutePath}")
        val mSaveUri = Uri.fromFile(file)
        Log.d("aaa","头像路径 ${file.absolutePath}")
        if (mSaveUri != null) {
            var outputStream: OutputStream? = null
            try {
                outputStream = contentResolver.openOutputStream(mSaveUri)
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                }
                viewModel.postImage(file){
                    val intent = Intent(this, MineFragment::class.java)
                    intent.putExtra("image_path",file.absolutePath)
                    setResult(Activity.RESULT_OK, intent)
                    outputStream?.flush()
                    finish()
                }
            } catch (ex: IOException) {
                Log.e("aaa", "Cannot open file: $mSaveUri", ex)
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    private fun whenBack(){
        if (isNetworkConnected(this) == NetworkState.NONE) {
            MessageTitleOkDialogUtil().initMessageTitleOkDialog(this) { finish() }
                .setTitle("网络未连接")
                .setMessage("当前网络未连接，您选择的图片将无法保存")
                .setOver("确定")
                .show()
        } else {
            generateUriAndReturn()
        }
    }
}
