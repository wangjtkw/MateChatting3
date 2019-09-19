package com.example.matechatting.mainprocess.milelist


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.*
import com.example.matechatting.base.BaseFragment
import com.example.matechatting.bean.UserBean
import com.example.matechatting.databinding.FragmentMileListBinding
import com.example.matechatting.mainprocess.chatting.ChattingActivity
import com.example.matechatting.mainprocess.infodetail.InfoDetailActivity
import com.example.matechatting.mainprocess.login.LoginActivity
import com.example.matechatting.mainprocess.main.MainActivity
import com.example.matechatting.mainprocess.main.MyOnTouchListener
import com.example.matechatting.mainprocess.milelistsearch.MileListSearchActivity
import com.example.matechatting.myview.SideView
import com.example.matechatting.utils.*
import com.example.matechatting.utils.statusbar.StatusBarUtil
import java.util.*
import kotlin.collections.ArrayList

class MileListFragment : BaseFragment() {
    private lateinit var binding: FragmentMileListBinding
    private lateinit var toolbar: Toolbar
    private lateinit var sideView: SideView
    private lateinit var unLoginLayout: LinearLayout
    private lateinit var loginLayout: LinearLayout
    private lateinit var loginButton: Button
    private lateinit var recycler: RecyclerView
    private lateinit var letterText: TextView
    private lateinit var adapter: MileListRecyclerAdapter
    private lateinit var viewModel: MileListViewModel
    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter
    private lateinit var searchButton: Button
    private lateinit var newFriendLayoutCallback: (Int) -> Unit
    private lateinit var newFriendButtonCallback: (Int) -> Unit
    private lateinit var newChattingCallback: (UserBean) -> Unit
    private lateinit var friendLayoutCallback: (Int) -> Unit

    private var newFriendNull = true
    private var newChattingNull = true
    private var friendNull = true

    private var changing = false
    //    private var
    private val myOnTouchListener = object : MyOnTouchListener {
        override fun onTouch(isScroll: Boolean) {
            val isLogin = isLogin
            if (isLogin) {
                if (isScroll && !changing) {
                    changing = true
                    sideView.visibility = View.GONE
                } else if (!isScroll) {
                    changing = false
                    sideView.visibility = View.VISIBLE
                    sideView.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.side_view_show)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mile_list, container, false)
        StatusBarUtil.setStatusBarDarkTheme(requireActivity(), true)
        init()
        initCallback()
        initRecycler()
        initReceiver()
        initSearchButton()
        return binding.root
    }

    override fun initView() {
        toolbar = binding.mileListToolbar
        sideView = binding.mileListSideview
        unLoginLayout = binding.mileListUnloginLayout
        loginLayout = binding.mileListLoginLayout
        loginButton = binding.mileListLoginButton
        recycler = binding.mileListRecycler
        letterText = binding.mileListLetter
        searchButton = binding.homeButtonSearch
        (requireActivity() as? MainActivity)?.registerMyOnTouchListener(myOnTouchListener)
        initSideView()
        val factory = InjectorUtils.provideMileListViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(MileListViewModel::class.java)
    }

    private fun initSearchButton() {
        searchButton.setOnClickListener {
            val intent = Intent(requireActivity(), MileListSearchActivity::class.java)
            requireActivity().startActivityForResult(intent, 0x999)
        }
    }

    private fun initData() {
        viewModel.getAllNewFriend {
            newFriendNull = it.isEmpty()
            canVisible()
            adapter.freshNewFriends(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.getAllNewChatting {
            Log.d("aaa","返回的getAllNewChatting $it")
            newChattingNull = it.isEmpty()
            canVisible()
            adapter.freshNewChattings(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.getAllFriend {
            friendNull = it.isEmpty()
            val array = ArrayList(it)
            canVisible()
            if (it.isNotEmpty()) {
                Collections.sort(array, object : Comparator<UserBean> {
                    override fun compare(p0: UserBean?, p1: UserBean?): Int {
                        if (p0 != null && p1 != null) {
                            return PinyinUtil.getPinyin(p0.name).compareTo(PinyinUtil.getPinyin(p1.name))
                        }
                        return 0
                    }
                })
            }
            adapter.frashFriends(array)
            adapter.notifyDataSetChanged()
        }
    }

    private fun canVisible() {
        if (newFriendNull && newChattingNull && friendNull) {
            loginLayout.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            loginLayout.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }

    private fun initCallback() {
        newFriendLayoutCallback = {
            val intent = Intent(requireActivity(), InfoDetailActivity::class.java)
            intent.putExtra("id", it)
            intent.putExtra("subject", NEW_FRIEND)
            requireActivity().startActivityForResult(intent, 0x999)
        }
        newFriendButtonCallback = {
            if (isNetworkConnected(requireContext()) == NetworkState.NONE) {
                ToastUtilWarning().setToast(requireContext(), "当前网络未连接")
            } else {
                MainActivity.service?.acceptFriend(it)
                adapter.notifyItemRemoved(it)
                adapter.notifyItemRangeChanged(it, 1)
            }
        }
        newChattingCallback = {
            val intent = Intent(requireActivity(), ChattingActivity::class.java)
            intent.putExtra("id", it.id)
            intent.putExtra("name", it.name)
            requireActivity().startActivityForResult(intent, 0x999)
        }
        friendLayoutCallback = {
            val intent = Intent(requireActivity(), InfoDetailActivity::class.java)
            Log.d("aaa", "测试 $it")
            intent.putExtra("id", it)
            intent.putExtra("subject", FRIEND)
            requireActivity().startActivityForResult(intent, 0x999)
        }
    }

    private fun initRecycler() {
        adapter = MileListRecyclerAdapter(
            newFriendLayoutCallback,
            newFriendButtonCallback,
            newChattingCallback,
            friendLayoutCallback
        )
        recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    /**
     * 登陆后获取数据
     * 未实现
     */
    override fun initLogin() {
        unLoginLayout.visibility = View.GONE
        initData()
    }

    override fun initNotLogin() {
        loginLayout.visibility = View.GONE
        unLoginLayout.visibility = View.VISIBLE
        recycler.visibility = View.GONE
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        loginButton.setOnClickListener {
            transferActivity(intent, LOGIN_REQUEST_CODE)
        }
    }

    private fun transferActivity(intent: Intent, requestCode: Int) {
        requireActivity().startActivityForResult(intent, requestCode)
    }


    private fun initSideView() {
        sideView.setOnTouchingLetterChangedListener(object : SideView.Companion.OnTouchingLetterChangedListener {
            override fun onNotTouching() {
                letterText.visibility = View.GONE
            }

            override fun onTouchingLetterChanged(str: String) {
                letterText.visibility = View.VISIBLE
                letterText.text = str
                val position = adapter.scrollToPosition(str)
                if (position != -1) {
                    recycler.smoothScrollToPosition(position)
                }

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? MainActivity)?.unregisterMyOnTouchListener(myOnTouchListener)
    }

    override fun onStart() {
        super.onStart()
        Log.d("aaa", "onStart")
        myRegisterReceiver()

    }

    override fun onStop() {
        super.onStop()
        Log.d("aaa", "onStop")
        requireActivity().unregisterReceiver(receiver)
    }

    private fun myRegisterReceiver() {
        intentFilter = IntentFilter()
        intentFilter.addAction(ACCEPT_FRIEND_ACTION)
        intentFilter.addAction(HAS_NEW_MESSAGE_ACTION)
        intentFilter.addAction(ADD_FRIEND_REQUEST_BROADCAST_ACTION)
        intentFilter.addAction(ON_LINE_FRIEND)
        intentFilter.addAction(LOG_IN_ACTION)
        intentFilter.addAction(LOG_OUT_ACTION)
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    private fun initReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                initData()
//                when (p1?.getStringExtra("subject") ?: "") {
//                    HAS_NEW_FRIEND -> {
//
//                    }
//                }
            }
        }
    }


}

