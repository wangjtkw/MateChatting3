package com.example.matechatting.mainprocess.album

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.CLIP_REQUEST_CODE
import com.example.matechatting.R
import com.example.matechatting.base.BaseActivity
import com.example.matechatting.databinding.ActivityAlbumBinding
import com.example.matechatting.mainprocess.cliphead.ClipActivity
import com.example.matechatting.utils.AlbumImage
import com.example.matechatting.utils.statusbar.StatusBarUtil

class AlbumActivity : BaseActivity<ActivityAlbumBinding>() {
    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var adapter: AlbumRecyclerAdapter
    private lateinit var back:FrameLayout
    private lateinit var recyclerCallBack:(url:String) ->Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setStatusBarDarkTheme(this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, this.getColor(R.color.bg_ffffff))
        }
        initBinding()
        canSlideFinish(true)
        initCallBack()
        initView()
        initData()
        initBack()
    }

    private fun initCallBack(){
        recyclerCallBack = {
            val intent = Intent(this, ClipActivity::class.java)
            intent.putExtra("image_uri",it)
            startActivityForResult(intent, CLIP_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CLIP_REQUEST_CODE && data != null)
        {
            setResult(Activity.RESULT_OK,data)
            finish()
        }
    }

    private fun initBack(){
        back.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        albumRecyclerView = binding.albumRecycler
        back = binding.albumBack
        adapter = AlbumRecyclerAdapter(recyclerCallBack)
        val layout = GridLayoutManager(this, 3)
        albumRecyclerView.layoutManager = layout
        albumRecyclerView.adapter = adapter
    }

    private fun initData() {
        val album = AlbumImage()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            album.checkPermission(this) {
                adapter.freshData(it)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_album
    }
}
