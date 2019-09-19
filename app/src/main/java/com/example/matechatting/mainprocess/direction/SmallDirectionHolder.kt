package com.example.matechatting.mainprocess.direction

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.util.containsKey
import com.example.matechatting.R
import com.example.matechatting.base.BaseHolder
import com.example.matechatting.base.BaseSource
import com.example.matechatting.bean.Direction
import com.example.matechatting.bean.SaveDirectionBean
import com.example.matechatting.databinding.ItemDirectionBinding
import com.example.matechatting.utils.ToastUtilWarning

class SmallDirectionHolder(private val bigDirectionId: Int, private val binding: ItemDirectionBinding) :
    BaseHolder(binding) {

    override fun <T> bind(t: T) {
        if (t is SmallDirectionSource) {
            val bean = t.normal
            initView(bean)
        }
    }

    private fun initView(bean: SaveDirectionBean.NormalDirection) {
        if (bean.direction?.directionName.isNullOrEmpty()) {
            binding.itemDirectionText.visibility = View.GONE
        } else {
            binding.itemDirectionText.visibility = View.VISIBLE
            binding.itemDirectionText.text = bean.direction?.directionName
        }
        initFlow(bean.smallDirection)
    }

    private fun initFlow(bean: List<Direction>?) {
        if (bean.isNullOrEmpty()) {
            return
        }
        for (s: Direction in bean) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_direction_lable, null)
            val layout = view.findViewById<FrameLayout>(R.id.item_direction_layout)
            val textView = view.findViewById<TextView>(R.id.item_direction_text)

            if (!s.directionName.isNullOrEmpty()) {
                textView.text = s.directionName
            }
            if (s.isSelect){
                layout.background = context.getDrawable(R.drawable.shape_direction_lable_selected)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextColor(context.getColor(R.color.text_ffffff))
                }
            }else{
                layout.background = context.getDrawable(R.drawable.shape_direction_lable_unselected)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextColor(context.getColor(R.color.text_555555))
                }
            }
            layout.setOnClickListener {

                if (!DirectionNewActivity.saveMap.containsKey(s.id) && DirectionNewActivity.saveMap.size() >= 3) {
                    ToastUtilWarning().setToast(context, "最多只能选择三个")
                } else {
                    setBackground(s, layout, textView)
                }
                DirectionNewActivity.clickCallback()
            }
            binding.itemDirectionFlowLayout.addView(view)
        }
    }

    private fun setBackground(s: Direction, layout: FrameLayout, textView: TextView) {
        if (s.isSelect) {
            s.isSelect = false
            layout.background = context.getDrawable(R.drawable.shape_direction_lable_unselected)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(context.getColor(R.color.text_555555))
            }
            DirectionNewActivity.saveMap.delete(s.id)
            DirectionNewActivity.resultMap.delete(s.id)
        } else {
            s.isSelect = true
            layout.background = context.getDrawable(R.drawable.shape_direction_lable_selected)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(context.getColor(R.color.text_ffffff))
            }
            DirectionNewActivity.saveMap.put(s.id, bigDirectionId)
            DirectionNewActivity.resultMap.put(s.id, s.directionName)
        }
    }

}

data class SmallDirectionSource(
    val normal: SaveDirectionBean.NormalDirection,
    val smallClickCallback: () -> Unit
) : BaseSource()