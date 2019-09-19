package com.example.matechatting.mainprocess.direction

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.util.SparseIntArray
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.matechatting.DIRECT_REQUEST_CODE
import com.example.matechatting.MyApplication
import com.example.matechatting.R
import com.example.matechatting.base.BaseActivity
import com.example.matechatting.bean.BigDirectionBean
import com.example.matechatting.databinding.ActivityDirectionNewBinding
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.ToastUtilWarning
import com.example.matechatting.utils.dialog.OKDialogUtil
import com.example.matechatting.utils.isNetworkConnected
import com.example.matechatting.utils.statusbar.StatusBarUtil
import java.lang.StringBuilder

class DirectionNewActivity : BaseActivity<ActivityDirectionNewBinding>() {
    private lateinit var back: FrameLayout
    private lateinit var recycler: RecyclerView
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: BigDirectionAdapter
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var viewModel: DirectionActivityViewModel
    private lateinit var recyclerCallback: (Int) -> Unit
    private lateinit var toolbarTitle: TextView
    private lateinit var bigDirectionArray: List<BigDirectionBean>
    private lateinit var saveFrame: FrameLayout
    private var saveTemp: SparseIntArray? = null

    private var token = ""
    private var isFirst = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = intent.getStringExtra("token") ?: ""
        if (token.isNotEmpty()) {
            isFirst = true
        }
        initBinding()
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setStatusBarDarkTheme(this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, this.getColor(R.color.bg_ffffff))
        }
        canSlideFinish(false)
        initSaveData()
        initView()
        initCallback()
        initRecycler()
        initData()
        initBack()
        initSave()
    }

    private fun initSaveData() {
        saveTemp = DirectionNewActivity.saveMap.clone()
    }

    private fun initView() {
        binding.apply {
            back = directionToolbarBack
            recycler = directionRecycler
            viewPager = directionViewpager
            toolbarTitle = directionToolbarTitle
            saveFrame = directionToolbarSave
        }
        val factory = InjectorUtils.provideDirectionActivityViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(DirectionActivityViewModel::class.java)
    }

    private fun initRecycler() {
        adapter = BigDirectionAdapter(recyclerCallback)
        recycler.adapter = adapter
        clickCallback = {
            adapter.setIsSelect()
        }
    }

    private fun initData() {
        viewModel.getBigDirection {
            if (!it.isNullOrEmpty()) {
                bigDirectionArray = it
                toolbarTitle.text = it[0].directionName
                initViewPager(it)
                adapter.freshData(it)
            }
        }
    }

    private fun initViewPager(list: List<BigDirectionBean>) {
        fragmentList = ArrayList()
        for (big: BigDirectionBean in list) {
            fragmentList.add(DirectionNewFragment.newInstance(big.id))
        }
        viewPager.adapter = PagerAdapter(fragmentList, supportFragmentManager)
        viewPager.currentItem = 0
        setViewPagerListener()
    }

    private fun initCallback() {
        recyclerCallback = {
            setCurrentPosition(it)
            toolbarTitle.text = bigDirectionArray[it].directionName
        }
    }

    private fun initSave() {
        saveFrame.setOnClickListener {
            if (isNetworkConnected(this) == NetworkState.NONE) {
                ToastUtilWarning().setToast(this, "当前网络未连接")
            } else {
                if (saveMap.isNotEmpty()) {
                    viewModel.saveDirection(saveTemp, token) {
                        saveSuccess()
                    }
                } else {
                    ToastUtilWarning().setToast(this, "请先选择方向")
                }

            }
        }
    }

    private fun saveSuccess() {
        OKDialogUtil().initOKDialog(this, "保存成功") {
            val intent = Intent()
            val sb = StringBuilder()
            resultMap.forEach { key, value ->
                sb.append(" ").append(value)
            }
            Log.d("aaa","保存的方向 $sb")
            intent.putExtra("direction", sb.toString().trim())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setCurrentPosition(position: Int) {
        viewPager.currentItem = position
    }

    private fun setViewPagerListener() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                adapter.setCurrentPosition(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        })
    }

    private fun initBack() {
        back.setOnClickListener {
            finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_direction_new
    }

    override fun onDestroy() {
        super.onDestroy()
        saveMap.clear()
    }

    companion object {
        val saveMap = SparseIntArray()
        val resultMap = SparseArray<String>()
        var clickCallback: () -> Unit = {}
    }
}
