package com.example.matechatting.mainprocess.chatting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.HAS_NEW_MESSAGE_ACTION
import com.example.matechatting.MyApplication
import com.example.matechatting.base.BaseActivity
import com.example.matechatting.bean.ChattingBean
import com.example.matechatting.databinding.ActivityChattingBinding
import com.example.matechatting.listener.EditTextTextChangeListener
import com.example.matechatting.mainprocess.main.MainActivity
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.ToastUtilWarning
import com.example.matechatting.utils.isNetworkConnected
import com.example.matechatting.utils.statusbar.StatusBarUtil
import java.sql.Timestamp


class ChattingActivity : BaseActivity<ActivityChattingBinding>() {
    private lateinit var back: FrameLayout
    private lateinit var title: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var edit: EditText
    private lateinit var send: Button
    private lateinit var adapter: ChattingAdapter
    private lateinit var viewModel: ChattingViewModel
    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter


    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setStatusBarDarkTheme(this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, this.getColor(com.example.matechatting.R.color.bg_ffffff))
        }
        id = intent.getIntExtra("id", 0)
        name = intent.getStringExtra("name") ?: ""

        Log.d("bbb", "ChattingActivity onCreate")
        canSlideFinish(true, true) {
            updateState {
                finish()
            }
        }
        initBinding()
        initView()
        updateState()
        initRecycler()
        initEdit()
        initSend()
        initReceiver()
        initBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("bbb", "ChattingActivity onDestroy")

        id = 0
    }

    private fun initView() {
        val factory = InjectorUtils.provideChattingViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(ChattingViewModel::class.java)
        binding.apply {
            back = chattingToolbarBack
            title = chattingToolbarTitle
            recycler = chattingRecycler
            edit = chattingMessageEdit
            send = chattingSendButton
        }
        title.text = name
    }

    private fun updateState(callback: () -> Unit = {}) {
        viewModel.updateState(id, callback)
    }

    private fun initRecycler() {
        adapter = ChattingAdapter()
        val layoutManager = LinearLayoutManager(this)
//        layoutManager.stackFromEnd = true
//        layoutManager.reverseLayout  = true
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
        recycler.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                recycler.post {
                    if (adapter.itemCount > 0) {
                        recycler.scrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
        }
        viewModel.getData(id, MyApplication.getUserId()!!)
        viewModel.newsList.observe(this, Observer {
            adapter.frashDatas(it)
            recycler.scrollToPosition(it.size - 1)
        })
    }

    private fun initEdit() {
        edit.addTextChangedListener(
            EditTextTextChangeListener({
                if (it.isEmpty()) {
                    send.isEnabled = false
                    send.background =
                        this.getDrawable(com.example.matechatting.R.drawable.shape_send_button_corner_disabled)
                } else {
                    send.isEnabled = true
                    send.background =
                        this.getDrawable(com.example.matechatting.R.drawable.shape_send_button_corner_available)
                }
            })
        )
    }

    private fun initSend() {
        send.setOnClickListener {
            if (isNetworkConnected(this) == NetworkState.NONE) {
                ToastUtilWarning().setToast(this, "当前网络未连接")
            } else {
                sendMessage()
            }
        }
    }

    private fun sendMessage() {
        val msg = edit.text.toString()
        val clear = SpannableStringBuilder("")
        edit.text = clear
        val bean = ChattingBean(
            id,
            MyApplication.getUserId()!!,
            msg,
            true,
            Timestamp(System.currentTimeMillis()).toString(),
            true
        )
        viewModel.insertMessage(bean)
        MainActivity.service?.sendMsg(msg, id)
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
        intentFilter.addAction(HAS_NEW_MESSAGE_ACTION)
        registerReceiver(receiver, intentFilter)
    }

    private fun initReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val bean = (p1?.getSerializableExtra("subject"))as? ChattingBean
                if (bean != null) {
                    viewModel.insertMessage(bean)
                }
            }
        }
    }


    private fun initBack() {
        back.setOnClickListener {
            updateState {
                finish()
            }
        }
    }

    override fun getLayoutId(): Int {
        return com.example.matechatting.R.layout.activity_chatting
    }

    companion object {
        var id = 0
    }
}
