package com.example.matechatting.mainprocess.infodetail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import com.example.matechatting.*
import com.example.matechatting.base.BaseActivity
import com.example.matechatting.bean.UserBean
import com.example.matechatting.databinding.ActivityInfoDetailBinding
import com.example.matechatting.mainprocess.chatting.ChattingActivity
import com.example.matechatting.mainprocess.main.MainActivity
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.ToastUtilWarning
import com.example.matechatting.utils.isNetworkConnected
import com.example.matechatting.utils.statusbar.StatusBarUtil
import java.util.*


class InfoDetailActivity : BaseActivity<ActivityInfoDetailBinding>() {
    private lateinit var back: FrameLayout
    private lateinit var viewModel: InfoDetailViewModel
    private lateinit var changeButton: Button
    private lateinit var chattingButton: Button
    private lateinit var acceptButton: Button
    private lateinit var userBean: UserBean
    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter
    private lateinit var headImage: ImageView

    private var subject = 0
    private var id = 0
    private var isChatting = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getIntExtra("id", 0)
        subject = intent.getIntExtra("subject", 0)
        isChatting = intent.getBooleanExtra("is_chatting", false)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setStatusBarDarkTheme(this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, this.getColor(R.color.bg_ffffff))
        }
        canSlideFinish(true)

        initBinding()
        initView()
        initBack()
        initReceiver()
    }

    private fun initView() {
        val factory = InjectorUtils.provideInfoDetailViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(InfoDetailViewModel::class.java)
        back = binding.infoDetailBack
        headImage = binding.infoDetailHeadImage
        viewModel.getDetail(id, headImage) {
            userBean = it
            initButton()
        }
        binding.viewmodel = viewModel

    }

    private fun initButton() {
        changeButton = binding.infoDetailChangeButton //交换
        chattingButton = binding.infoChattingButton   //聊天
        acceptButton = binding.infoDetailAcceptButton //同意
        when (subject) {
            HOME_ITEM -> {
                changeButton.visibility = View.VISIBLE
                chattingButton.visibility = View.GONE
                acceptButton.visibility = View.GONE
                initChangeButton()
            }
            NEW_FRIEND -> {
                changeButton.visibility = View.GONE
                chattingButton.visibility = View.GONE
                acceptButton.visibility = View.VISIBLE
                initAccept()
            }
            NEW_CHATTING, FRIEND -> {
                changeButton.visibility = View.GONE
                chattingButton.visibility = View.VISIBLE
                acceptButton.visibility = View.GONE
                initChatting()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    override fun onStart() {
        super.onStart()
        myRegisterReceiver()
    }

    private fun myRegisterReceiver() {
        intentFilter = IntentFilter()
        intentFilter.addAction(ADD_FRIEND_REQUEST_BROADCAST_ACTION)
        registerReceiver(receiver, intentFilter)
    }

    private fun initAccept() {
        acceptButton.setOnClickListener {
            if (isNetworkConnected(this) == NetworkState.NONE) {
                ToastUtilWarning().setToast(this, "当前网络未连接")
            } else {
                viewModel.updateState(userBean, 4) {
                    MainActivity.service?.acceptFriend(id)
                    finish()
                }
            }
        }
    }

    private fun initChatting() {
        chattingButton.setOnClickListener {
            val intent = Intent(this, ChattingActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", userBean.name)
            intent.putExtra("headImage", userBean.headImage)
            transferActivity(intent, 0x999)
        }
    }

    private fun transferActivity(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    private fun initChangeButton() {
        changeButton.setOnClickListener {
            MainActivity.service?.addFriend(id, UUID.randomUUID().toString())
        }
    }

    private fun initBack() {
        back.setOnClickListener {
            finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_info_detail
    }

    private fun initReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when (p1?.getIntExtra("subject", 0)) {
                    1 -> {
                        ToastUtilWarning().setToast(this@InfoDetailActivity, "您已发送过请求")
                    }
                    2 -> {
                        ToastUtilWarning().setToast(this@InfoDetailActivity, "你们已经是好友")
                    }
                    3 -> {
                        ToastUtilWarning().setToast(this@InfoDetailActivity, "对方请求添加您为好友")
                    }
                    4 -> {
                        ToastUtilWarning().setToast(this@InfoDetailActivity, "不可以加自己为好友")
                    }
                }
            }
        }
    }

}
