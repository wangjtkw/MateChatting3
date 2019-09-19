package com.example.matechatting.mainprocess.homesearch


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.ADD_FRIEND_REQUEST_BROADCAST_ACTION
import com.example.matechatting.HOME_ITEM
import com.example.matechatting.LOGIN_REQUEST_CODE

import com.example.matechatting.R
import com.example.matechatting.base.BaseFragment
import com.example.matechatting.databinding.FragmentResultBinding
import com.example.matechatting.mainprocess.infodetail.InfoDetailActivity
import com.example.matechatting.mainprocess.login.LoginActivity
import com.example.matechatting.mainprocess.main.MainActivity
import com.example.matechatting.utils.ToastUtilWarning
import java.util.*

class ResultFragment : BaseFragment() {
    private lateinit var binding: FragmentResultBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeSearchResultAdapter
    private lateinit var callbackPersonButton: (Int) -> Unit
    private lateinit var callbackPersonLayout: (Int) -> Unit
    private lateinit var callbackLoadMore: (Int) -> Unit
    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter
    private var page = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        init()
        initRecycler()
        initReceiver()
        return binding.root
    }

    override fun initView() {
        callbackLoadMore = {
            loadMore()
        }
        recyclerView = binding.searchResultRecycler
    }

    override fun onResume() {
        super.onResume()
        init()
        setCallbackToAdapter()
    }

    private fun initRecycler() {
        adapter = HomeSearchResultAdapter(callbackPersonButton, callbackPersonLayout, { loadMore() })
        val array = (requireActivity() as HomeSearchActivity).resultArray
        if (array.size < 20) {
            adapter.needGone = true
        }
        adapter.frashData(array)
        Log.d("aaa", "resultArray.observe + $array")

        recyclerView.adapter = adapter
    }

    private fun loadMore() {
        val activity = requireActivity() as HomeSearchActivity
        activity.viewModel.getResult(activity.key, getPage()) {
            if (it.size < 20) {
                adapter.needGone = true
            }
            adapter.frashData(it)

        }
    }

    private fun getPage(): Int {
        return ++page
    }

    private fun setCallbackToAdapter() {
        adapter.callbackPersonButton = callbackPersonButton
        adapter.callbackPersonLayout = callbackPersonLayout
    }

    override fun initLogin() {
        Log.d("aaa","initLogin")
        callbackPersonButton = {
            Log.d("aaa", "点击的id $it")
            MainActivity.service?.addFriend(it, UUID.randomUUID().toString())
        }
        callbackPersonLayout = {
            val intent = Intent(requireActivity(), InfoDetailActivity::class.java)
            intent.putExtra("id", it)
            intent.putExtra("subject", HOME_ITEM)
            requireActivity().startActivityForResult(intent, 0x999)
        }
    }

    override fun initNotLogin() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        callbackPersonButton = {
            transferActivity(intent, LOGIN_REQUEST_CODE)
        }
        callbackPersonLayout = {
            transferActivity(intent, LOGIN_REQUEST_CODE)
        }
    }

    private fun transferActivity(intent: Intent, requestCode: Int) {
        requireActivity().startActivityForResult(intent, requestCode)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(receiver)
    }

    override fun onStart() {
        super.onStart()
        myRegisterReceiver()
    }

    private fun myRegisterReceiver() {
        intentFilter = IntentFilter()
        intentFilter.addAction(ADD_FRIEND_REQUEST_BROADCAST_ACTION)
//        receiver = AddFriendBroadcastReceiver()
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    private fun initReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when (p1?.getIntExtra("subject", 0)) {
                    1 -> {
                        ToastUtilWarning().setToast(requireContext(), "您已发送过请求")
                    }
                    2 -> {
                        ToastUtilWarning().setToast(requireContext(), "你们已经是好友")
                    }
                    3 -> {
                        ToastUtilWarning().setToast(requireContext(), "对方请求添加您为好友")
                    }
                    4 -> {
                        ToastUtilWarning().setToast(requireContext(), "不可以加自己为好友")
                    }
                }
            }
        }
    }


}
