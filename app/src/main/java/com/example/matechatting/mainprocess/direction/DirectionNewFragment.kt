package com.example.matechatting.mainprocess.direction


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matechatting.R
import com.example.matechatting.utils.InjectorUtils
import com.nex3z.flowlayout.FlowLayout

class DirectionNewFragment : Fragment() {
    private lateinit var recycler: RecyclerView
    private var bigDirectionId = 0
    private lateinit var adapter: SmallDirectionAdapter
    private lateinit var smallClickCallback: () -> Unit
    private lateinit var viewModel: DirectionFragmentViewModel
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bigDirectionId = arguments?.getInt("big_direction_id") ?:throw Exception("id is 0")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_direction_new, container, false)
        initCallback()
        initView(view)
        initData()
        return view
    }

    private fun initCallback() {
        smallClickCallback = {
            changeView(layoutManager)
        }
    }

    private fun initView(view: View) {
        val factory = InjectorUtils.provideDirectionFragmentViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(DirectionFragmentViewModel::class.java)
        adapter = SmallDirectionAdapter(bigDirectionId,smallClickCallback)
        layoutManager = LinearLayoutManager(requireContext())
        recycler = view.findViewById(R.id.fragment_direction_recycler)
        recycler.setItemViewCacheSize(20)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter

    }

    private fun changeView(layoutManager: LinearLayoutManager) {
        for (i: Int in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i)as? FlowLayout
            val list = child?.children ?: return
            for (view:View in list){
                val text = view.findViewById<TextView>(R.id.item_direction_text)
                val layout = view.findViewById<FrameLayout>(R.id.item_direction_layout)
                text.setTextColor(Color.parseColor("#665522"))

            }
        }

    }

    private fun initData() {
        viewModel.getSmallDirection(bigDirectionId) {
            Log.d("aaa","搜索总结果 $it")
            if (!it.normalDirectionList.isNullOrEmpty()) {
                adapter.freshData(it.normalDirectionList!!)
            }
        }
    }

    companion object {
        fun newInstance(bigDirectionId: Int): DirectionNewFragment {
            val classifyFragment = DirectionNewFragment()
            val args = Bundle()
            args.putInt("big_direction_id", bigDirectionId)
            classifyFragment.arguments = args
            return classifyFragment
        }
    }
}
