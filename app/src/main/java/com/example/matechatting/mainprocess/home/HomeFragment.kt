package com.example.matechatting.mainprocess.home


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.*
import com.example.matechatting.base.BaseFragment
import com.example.matechatting.databinding.FragmentHomeBinding
import com.example.matechatting.listener.RecyclerScrollListener
import com.example.matechatting.mainprocess.homesearch.HomeSearchActivity
import com.example.matechatting.mainprocess.infodetail.InfoDetailActivity
import com.example.matechatting.mainprocess.login.LoginActivity
import com.example.matechatting.mainprocess.main.MainActivity
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.ToastUtilWarning
import java.util.*

/**
 * 交换名片按钮点击事件未完成
 * @link [initLogin]
 */
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeButton: Button
    private lateinit var viewModel: HomeItemViewModel
    private lateinit var adapter: HomeItemAdapter
    private lateinit var callbackPersonButton: (Int) -> Unit
    private lateinit var callbackPersonLayout: (Int) -> Unit
    private lateinit var scrollListener: RecyclerScrollListener
    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        init()
        initRecyclerView()
        setCallbackToAdapter()
        initReceiver()
        return binding.root
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

    override fun initView() {
        val factory = InjectorUtils.provideHomeItemViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(HomeItemViewModel::class.java)
        recyclerView = binding.homeRecyclerView
        homeButton = binding.homeButtonSearch
        initSearchButton()
    }

    private fun initScrollListener(layoutManager: LinearLayoutManager) {
        scrollListener = object : RecyclerScrollListener(layoutManager) {

            override fun isLastPage(): Boolean {
                return PAGE.isEmpty()
            }

            override fun loadMoreItems(callback: (Boolean) -> Unit) {
                viewModel.getDataNormal(requireContext(),callback)
            }
        }
    }

    private fun initSearchButton() {
        homeButton.setOnClickListener {
            val intent = Intent(requireActivity(), HomeSearchActivity::class.java)
            transferActivity(intent, 0x999)
        }
    }

    private fun initRecyclerView() {
        adapter = HomeItemAdapter(callbackPersonButton, callbackPersonLayout)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = false
        initScrollListener(layoutManager)
        recyclerView.addOnScrollListener(scrollListener)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        viewModel.dataList.observe(this, Observer { list ->
            if (list != null) {
                adapter.freshData(list)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        init()
        viewModel.getDataNormal(requireContext())
        setCallbackToAdapter()
    }

    private fun transferActivity(intent: Intent, requestCode: Int) {
        requireActivity().startActivityForResult(intent, requestCode)
    }

    private fun setCallbackToAdapter() {
        adapter.callbackPersonButton = callbackPersonButton
        adapter.callbackPersonLayout = callbackPersonLayout
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

    override fun initLogin() {
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

}
