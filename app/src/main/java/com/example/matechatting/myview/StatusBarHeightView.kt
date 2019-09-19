package com.example.matechatting.myview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.matechatting.R

/**
 * 作者:东芝
 * 功能:状态栏高度View,用于沉浸占位
 */

class StatusBarHeightView : ConstraintLayout {
    private var statusBarHeight: Int = 0
    private var type: Int = 0

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }
        } else {
            //低版本 直接设置0
            statusBarHeight = 0
        }
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusBarHeightView)
            type = typedArray.getInt(R.styleable.StatusBarHeightView_use_type, 0)
            typedArray.recycle()
        }
        if (type == 1) {
            setPadding(paddingLeft, statusBarHeight, paddingRight, paddingBottom)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (type == 0) {
            setMeasuredDimension(
                View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                statusBarHeight
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}
