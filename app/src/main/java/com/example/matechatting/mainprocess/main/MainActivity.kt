package com.example.matechatting.mainprocess.main

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.matechatting.*
import com.example.matechatting.base.BaseActivity
import com.example.matechatting.base.BaseFragment
import com.example.matechatting.base.MyServiceConnection
import com.example.matechatting.databinding.ActivityMainBinding
import com.example.matechatting.mainprocess.home.HomeFragment
import com.example.matechatting.mainprocess.milelist.MileListFragment
import com.example.matechatting.mainprocess.mine.MineFragment
import com.example.matechatting.tcpprocess.service.NetService
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.statusbar.StatusBarUtil
import com.google.android.material.tabs.TabLayout


class MainActivity : BaseActivity<ActivityMainBinding>(), MainConstValue {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private val fragmentList = ArrayList<Fragment>()
    private val onTouchListeners = ArrayList<MyOnTouchListener>()
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setTranslucentStatus(this)
        super.onCreate(savedInstanceState)
        initBinding()
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000)
        }
        getLoginState()
        init()
        initViewPager()
        initTabLayout()
        listenNetwork()
        Log.d("aaa", "oncreate")
    }

    private fun init() {

        canSlideFinish(false)
        viewPager = binding.mainViewpager
        tabLayout = binding.mainTbLayout

    }

    private fun initViewPager() {
        fragmentList.add(HomeFragment())
        fragmentList.add(MileListFragment())
        fragmentList.add(MineFragment())
        viewPager.adapter = PagerAdapter(fragmentList, supportFragmentManager)
        viewPager.currentItem = 0
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
            }

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == 1) {
                    if (positionOffset != 0f) {
                        for (l: MyOnTouchListener in onTouchListeners) {
                            l.onTouch(true)
                        }
                    } else {
                        for (l: MyOnTouchListener in onTouchListeners) {
                            l.onTouch(false)
                        }
                    }
                }
            }
        })
    }

    private fun initTabLayout() {
        for (i: Int in 0 until tabText.size) {
            if (i == 0) {
                tabLayout.addTab(tabLayout.newTab().setText(tabText[0]).setIcon(tabSelectedDrawableIdList[0]))
            } else {
                tabLayout.addTab(tabLayout.newTab().setText(tabText[i]).setIcon(tabUnselectedDrawableList[i]))
            }
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.apply {
                    setIcon(tabUnselectedDrawableList[position])
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    setIcon(tabSelectedDrawableIdList[position])
                    viewPager.currentItem = position
                }
            }
        })
    }

    private fun getLoginState() {
        val sp = getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
        val isLogin = sp.getBoolean("isLogin", false)
        BaseFragment.isLogin = isLogin
        val account = sp.getString("account", "")
        BaseFragment.account = account ?: ""
        val factory = InjectorUtils.provideMainViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        if (isLogin) {
            initOtherProcess()
            Log.d("aaa","getLoginState 调用")
//            viewModel.getFriend {
//                val intent = Intent(ACCEPT_FRIEND_ACTION)
//                intent.putExtra("subject", 3)
//                this.sendBroadcast(intent)
//            }
            viewModel.getMineInfo()
        }
        viewModel.getDirection()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == LOGIN_REQUEST_CODE && data != null) {
            Log.d("aaa", "onActivityResult")
            getLoginState()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    fun registerMyOnTouchListener(myOnTouchListener: MyOnTouchListener) {
        onTouchListeners.add(myOnTouchListener)
    }

    fun unregisterMyOnTouchListener(myOnTouchListener: MyOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener)
    }

    /**
     * 初始化另一进程数据
     */
    private lateinit var serviceConnection: ServiceConnection

    private fun initOtherProcess() {
        initService()
    }

    private fun initService() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                service = TCPInterface.Stub.asInterface(p1)
            }

            override fun onServiceDisconnected(p0: ComponentName?) {}
        }
        val intent = Intent(this, NetService::class.java)
        this.startService(intent)
        if (!MyServiceConnection.isMainBind) {
            bindService(intent, MyServiceConnection.getInstance(), Context.BIND_AUTO_CREATE)
            MyServiceConnection.isMainBind = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PAGE.clear()
        if (MyServiceConnection.isMainBind){
            unbindService(MyServiceConnection.getInstance())
            MyServiceConnection.isMainBind = false
        }
    }

    companion object {
        var service: TCPInterface? = null
    }
}

interface MyOnTouchListener {
    fun onTouch(isScroll: Boolean)
}

