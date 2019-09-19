package com.example.matechatting.myview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.math.max

class FlowLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ViewGroup(context, attrs, defStyle) {

    /**
     * 存储所有的View
     */
    private val mAllViews = ArrayList<List<View>>()
    /**
     * 每一行的高度
     */
    private val mLineHeight = ArrayList<Int>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)

        // 如果是warp_content情况下，记录宽和高
        var width = 0
        var height = 0

        // 记录每一行的宽度与高度
        var lineWidth = 0
        var lineHeight = 0

        // 得到内部元素的个数
        val cCount = childCount

        for (i in 0 until cCount) {
            // 通过索引拿到每一个子view
            val child = getChildAt(i)
            // 测量子View的宽和高,系统提供的measureChild
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            // 得到LayoutParams
            val lp = child
                .layoutParams as MarginLayoutParams

            // 子View占据的宽度
            val childWidth = (child.measuredWidth + lp.leftMargin
                    + lp.rightMargin)
            // 子View占据的高度
            val childHeight = (child.measuredHeight + lp.topMargin
                    + lp.bottomMargin)

            // 换行 判断 当前的宽度大于 开辟新行
            if (lineWidth + childWidth > sizeWidth - paddingLeft - paddingRight) {
                // 对比得到最大的宽度
                width = max(width, lineWidth)
                // 重置lineWidth
                lineWidth = childWidth
                // 记录行高
                height += lineHeight
                lineHeight = childHeight
            } else
            // 未换行
            {
                // 叠加行宽
                lineWidth += childWidth
                // 得到当前行最大的高度
                lineHeight = max(lineHeight, childHeight)
            }
            // 特殊情况,最后一个控件
            if (i == cCount - 1) {
                width = max(lineWidth, width)
                height += lineHeight
            }
        }
        setMeasuredDimension(
            if (modeWidth == MeasureSpec.EXACTLY) sizeWidth else width + paddingLeft + paddingRight,
            if (modeHeight == MeasureSpec.EXACTLY) sizeHeight else height + paddingTop + paddingBottom//
        )

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mAllViews.clear()
        mLineHeight.clear()

        // 当前ViewGroup的宽度
        val width = width

        var lineWidth = 0
        var lineHeight = 0

        // 存放每一行的子view
        var lineViews: MutableList<View> = ArrayList()

        val cCount = childCount

        for (i in 0 until cCount) {
            val child = getChildAt(i)
            val lp = child
                .layoutParams as MarginLayoutParams

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            // 如果需要换行
            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - paddingLeft - paddingRight) {
                // 记录LineHeight
                mLineHeight.add(lineHeight)
                // 记录当前行的Views
                mAllViews.add(lineViews)

                // 重置我们的行宽和行高
                lineWidth = 0
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin
                // 重置我们的View集合
                lineViews = ArrayList()
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin
            lineHeight = max(
                lineHeight, childHeight + lp.topMargin
                        + lp.bottomMargin
            )
            lineViews.add(child)

        }// for end
        // 处理最后一行
        mLineHeight.add(lineHeight)
        mAllViews.add(lineViews)

        // 设置子View的位置

        var left = paddingLeft
        var top = paddingTop

        // 行数
        val lineNum = mAllViews.size

        for (i in 0 until lineNum) {
            // 当前行的所有的View
            lineViews = mAllViews[i] as MutableList<View>
            lineHeight = mLineHeight[i]

            for (j in lineViews.indices) {
                val child = lineViews[j]
                // 判断child的状态
                if (child.visibility == View.GONE) {
                    continue
                }

                val lp = child
                    .layoutParams as MarginLayoutParams

                val lc = left + lp.leftMargin
                val tc = top + lp.topMargin
                val rc = lc + child.measuredWidth
                val bc = tc + child.measuredHeight

                // 为子View进行布局
                child.layout(lc, tc, rc, bc)

                left += (child.measuredWidth + lp.leftMargin
                        + lp.rightMargin)
            }
            left = paddingLeft
            top += lineHeight
        }

    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
}

