package com.example.matechatting.myview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.example.matechatting.R
import com.example.matechatting.utils.DisplayUtils
import kotlin.math.max


class SideView : View {

    private val TAG = "projecttest.SideView"

    //画笔工具
    private lateinit var paint: Paint
    //当前选中的是哪一个
    private var choose = 0
    //字体大小
    private var textSize = 0

    //未选中字体颜色
    private var unChooseTextColor = 0
    //选中字体颜色
    private var chooseTextColor = 0
    //选中字体背景颜色
    private var chooseBGColor = 0
    //触摸监听器
    private var listener: OnTouchingLetterChangedListener? = null

    private var isWrap = false

    constructor(context: Context) : super(context) {}

    constructor(mContext: Context, attributeSet: AttributeSet?) : super(mContext, attributeSet) {
        if (attributeSet == null) {
            return
        }
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.SideView)
        paint = Paint()
        paint.isAntiAlias = true
        initAttr(array)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
    }

    /**
     * 初始化Attr相关数据
     * testSize:
     * unChooseTextColor:
     * chooseTextColor:
     * chooseBGColor:
     */
    private fun initAttr(array: TypedArray) {
        textSize = array.getDimensionPixelSize(
            R.styleable.SideView_textSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, resources.displayMetrics).toInt()
        )
        unChooseTextColor = array.getColor(R.styleable.SideView_unChooseTextColor, Color.parseColor("#464646"))
        chooseTextColor = array.getColor(R.styleable.SideView_chooseTextColor, Color.parseColor("#edeefe"))
        chooseBGColor = array.getColor(R.styleable.SideView_choose_BG_Color, Color.parseColor("#696efe"))
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val suggestedWidth = suggestedMinimumWidth
        val suggestedHeight = suggestedMinimumHeight

        val width = measureWidth(suggestedWidth, widthMeasureSpec)
        val height = measureHeight(suggestedHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measureWidth(defaultWidth: Int, measureSpec: Int): Int {
        val widthSpecMode = MeasureSpec.getMode(measureSpec)
        val widthSpecSize = MeasureSpec.getSize(measureSpec)
        var mWidth = 0
        when (widthSpecMode) {
            MeasureSpec.AT_MOST -> {
                var temp1 = 0f
                var maxWidth = 0f
                for (i: Int in 0 until A_Z.size) {
                    temp1 = paint.measureText(A_Z[i])
                    if (temp1 > maxWidth) {
                        maxWidth = temp1
                    }
                }
                mWidth = (maxWidth + 2).toInt()
            }
            MeasureSpec.EXACTLY -> {
                mWidth = widthSpecSize
            }
            MeasureSpec.UNSPECIFIED -> {
                mWidth = max(defaultWidth, widthSpecSize)
            }
        }
        return mWidth
    }

    private fun measureHeight(defaultHeight: Int, measureSpec: Int): Int {
        val heightSpecMode = MeasureSpec.getMode(measureSpec)
        val heightSpecSize = MeasureSpec.getSize(measureSpec)

        var totalHeight = 0
        when (heightSpecMode) {
            MeasureSpec.AT_MOST -> {
                isWrap = true
                for (i: Int in 0 until A_Z.size) {
                    totalHeight += (paint.measureText(A_Z[i].toString()) + DisplayUtils.dip2px(context, 12f)).toInt()
                }
            }
            MeasureSpec.EXACTLY -> {
                totalHeight = heightSpecSize
            }
            MeasureSpec.UNSPECIFIED -> {
                totalHeight = max(defaultHeight, heightSpecSize)
            }
        }
        return totalHeight
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        val mHeight = height
        val mWidth = width
        var singleHeight = 0
        singleHeight = if (isWrap) {
            mHeight / A_Z.size
        } else {
            mHeight / A_Z.size - 2
        }

        for (i: Int in 0 until A_Z.size) {
            paint.color = unChooseTextColor
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.isAntiAlias = true
            paint.textSize = textSize.toFloat()
            val xPos = mWidth / 2 - paint.measureText(A_Z[i].toString()) / 2
            val yPos = (singleHeight * i + singleHeight).toFloat()
            if (choose == i) {
                paint.color = chooseBGColor
                paint.style = Paint.Style.FILL
                val circleX = xPos + paint.measureText(A_Z[i]) / 2
                val circleY = yPos - paint.measureText(A_Z[i]) / 2
                canvas.drawCircle(circleX, circleY, (singleHeight / 2).toFloat(), paint)
                paint.color = chooseTextColor
            }
            if (i == 0) {
                canvas.drawText(A_Z[i], xPos, yPos + 7, paint)
            } else {
                canvas.drawText(A_Z[i], xPos, yPos, paint)
            }
            paint.reset()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        val action = event.action
        val y = event.y
        val oldChoose = choose
        val listener = this.listener ?: throw Exception("SideView's onTouchListener nut be null")
        val c = ((y / height) * A_Z.size).toInt()
        Log.d(TAG, c.toString())
        if (action == MotionEvent.ACTION_UP) {
            listener.onNotTouching()
        } else {
            if (oldChoose != c) {
                if (c >= 0 && c < A_Z.size) {
                    listener.onTouchingLetterChanged(A_Z[c].toString())
                }
                choose = c
                invalidate()
            }
        }
        return true
    }

    fun setOnTouchingLetterChangedListener(listener: OnTouchingLetterChangedListener) {
        this.listener = listener
    }


    companion object {
        val A_Z = arrayOf(
            "↑", "☆", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        )
//            , 35.toChar()


        interface OnTouchingLetterChangedListener {
            fun onTouchingLetterChanged(str: String)

            fun onNotTouching()
        }
    }

}