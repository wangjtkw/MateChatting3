package com.example.matechatting.mainprocess.forgetpassword

import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.matechatting.R
import com.example.matechatting.base.BaseActivity
import com.example.matechatting.databinding.ActivityForgetPasswordBinding
import com.example.matechatting.utils.statusbar.StatusBarUtil

/**
 * 未完成
 * 获取验证码
 * 判断是否为可用手机号
 * 输入6位验证码自动跳转或显示错误
 * error的显示未设置
 * 有bug @link[setCountdown]
 * @link[initNextButton]未实现
 */
class ForgetPasswordActivity : BaseActivity<ActivityForgetPasswordBinding>() {
    private lateinit var back: FrameLayout
    private lateinit var toolbarText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setStatusBarDarkTheme(this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, this.getColor(R.color.bg_ffffff))
        }
        canSlideFinish(true)
        initBinding()
        initView()
        replaceFragment(ForgetPasswordFragment(), "忘记密码")
        initBackButton()
    }

    private fun initView() {
        binding.apply {
            back = forgetBack
            toolbarText = forgetToolbarText
        }
    }

    fun replaceFragment(fragment: Fragment, text: String) {
        toolbarText.text = text
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        transaction.replace(R.id.forget_frame_layout, fragment)
        transaction.commit()
    }

    private fun initBackButton(){
        back.setOnClickListener {
            finish()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_forget_password
    }

    companion object{
        var account = ""
        var token = ""
    }
}
