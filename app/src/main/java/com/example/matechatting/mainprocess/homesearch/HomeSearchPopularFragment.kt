package com.example.matechatting.mainprocess.homesearch


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.matechatting.R
import com.nex3z.flowlayout.FlowLayout

class HomeSearchPopularFragment : Fragment() {
    private lateinit var flowLayout: FlowLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_search_popular, container, false)
        flowLayout = view.findViewById(R.id.search_flow_layout)
        initFlowLayout()
        return view
    }

    private fun initFlowLayout() {
        val array = labelArray
        for ((i, s: String) in array.withIndex()) {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_search_label, null)
            val layout = view.findViewById<FrameLayout>(R.id.item_search_layout)
            val textView = view.findViewById<TextView>(R.id.item_search_text)
            textView.text = s
            layout.setOnClickListener {
                val activity = (requireActivity() as HomeSearchActivity)
                activity.key = labelArray[i]
                activity.getData()
                Log.d("aaa","点击了 key:" + labelArray[i])
            }
            flowLayout.addView(view)
        }
    }

    companion object {
        private val labelArray = arrayOf("JAVA", "云计算", "IOS", "产品经理", "运维工程师", "Android", "系统分析员", "UI设计")
    }
}
