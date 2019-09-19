package com.example.matechatting.myview

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.ViewPager
import com.example.matechatting.base.BaseActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class SlideFinishLayout<T : ViewDataBinding> : LinearLayout {

    private var mTouchSlop: Int = 0//系统默认的滑动事件触发距离
    private var startX = 0
    private var startY = 0//触摸事件开始点
    private var moveDistanceX = 0
    private var moveDistanceY = 0//滑动的距离
    private var mVelocityTracker: VelocityTracker? = null//速度计算器
    private val XSPEED_MIN = 1000//手指在X方向滑动时的最小速度（px/s）

    private var mIgnoredViews = ArrayList<View>()//滑动忽略控件列表
    private var mViewPagers = LinkedList<ViewPager>()//该控件子控件中包含ViewPager的集合
    private var activity: BaseActivity<T>? = null//所有activity的基类
    private var needDoOther = false
    private var doOther: () -> Unit = {}

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : super(mContext, attributeSet) {
        init(context)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop
    }

    //和activity绑定
    fun attachToActivity(activity: BaseActivity<T>, needDoOther: Boolean = false, callback: () -> Unit = {}) {
        this.activity = activity
        this.needDoOther = needDoOther
        this.doOther = callback
        //将activity视图中的所有控件添加到这个控件中
        val decor = activity.window.decorView as ViewGroup
        val decorChild = decor.getChildAt(0) as ViewGroup
        decor.removeView(decorChild)
        addView(decorChild)
        decor.addView(this)
    }

    fun detachToActivity() {
        if (this.activity != null) {
            this.activity = null
        }
    }

    fun addIgnoredView(view: View) {
        if (!mIgnoredViews.contains(view)) {
            //防止因添加的是textView等本身没有处理touch事件的view，而引起viewGroup的onInterceptTouchEvent返回false，但是其onTouchEvent还是会执行的BUG
            view.isClickable = true
            mIgnoredViews.add(view)
        }
    }

    fun removeIgnoredView(v: View) {
        mIgnoredViews.remove(v)
    }

    fun clearIgnoredViews() {
        mIgnoredViews.clear()
    }

    private fun isInIgnoredView(ev: MotionEvent): Boolean {
        val rect = Rect()
        val location = IntArray(2)
        for (v in mIgnoredViews) {
            v.getLocationInWindow(location)
            rect.set(location[0], location[1], location[0] + v.measuredWidth, location[1] + v.measuredHeight)
            if (rect.contains(ev.x.toInt(), ev.y.toInt())) return true
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        //如果滑动的是viewpager或者是已添加的事件忽略view，就分发给子控件进行事件处理
        val mViewPager = getTouchViewPager(ev)
        if (mViewPager != null && mViewPager.currentItem != 0 || isInIgnoredView(ev)) {
            return false
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.rawX.toInt()
                startY = ev.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = ev.rawX.toInt()
                // 若满足此条件，屏蔽子类的touch事件
                if (moveX - startX > mTouchSlop && abs(ev.rawY.toInt() - startY) < mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.rawX.toInt()
                startY = ev.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                moveDistanceX = (ev.rawX - startX).toInt()
                moveDistanceY = (ev.rawY - startY).toInt()
            }
            MotionEvent.ACTION_UP -> {
                startX = 0
                startY = 0
                if (moveDistanceX > getScreenWidth() / 20
                    && moveDistanceX > 2 * abs(moveDistanceY)
                    && getScrollVelocity() > XSPEED_MIN
                ) {
                    if (needDoOther) {
                        doOther()
                    } else {
                        activity!!.finish()
                    }
                    return true
                }
                moveDistanceX = 0
                moveDistanceY = 0
                recycleVelocityTracker()
            }
            else -> {
            }
        }
        return true
    }

    private fun getScreenWidth(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    private fun recycleVelocityTracker() {
        mVelocityTracker!!.recycle()
        mVelocityTracker = null
    }

    private fun getScrollVelocity(): Int {
        mVelocityTracker!!.computeCurrentVelocity(1000)
        val velocity = mVelocityTracker!!.xVelocity.toInt()
        return Math.abs(velocity)
    }

    private fun getAllViewPager(parent: ViewGroup) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (child is ViewPager) {
                mViewPagers.add(child)
            } else if (child is ViewGroup) {
                getAllViewPager(child)
            }
        }
    }

    private fun getTouchViewPager(ev: MotionEvent): ViewPager? {
        if (mViewPagers.size == 0) {
            return null
        }
        val mRect = Rect()
        val location = IntArray(2)
        for (v in mViewPagers) {
            v.getLocationInWindow(location)
            mRect.set(location[0], location[1], location[0] + v.measuredWidth, location[1] + v.measuredHeight)
            if (mRect.contains(ev.x.toInt(), ev.y.toInt())) {
                return v
            }
        }
        return null
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) getAllViewPager(this)//布局子控件的时候获取其中的所有viewPager
    }


}