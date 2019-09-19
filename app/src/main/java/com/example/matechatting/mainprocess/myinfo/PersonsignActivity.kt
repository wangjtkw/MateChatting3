package com.example.matechatting.mainprocess.myinfo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.example.matechatting.R
import com.example.matechatting.base.BaseActivity
import com.example.matechatting.databinding.ActivityPersonSignBinding
import com.example.matechatting.listener.EditTextTextChangeListener
import com.example.matechatting.utils.statusbar.StatusBarUtil

class PersonsignActivity : BaseActivity<ActivityPersonSignBinding>() {
    private lateinit var tv_finish: TextView
    private lateinit var edittext: EditText
    private lateinit var back: FrameLayout
    private lateinit var tv_num: TextView
    private var slogan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slogan = intent.getStringExtra("slogan")?:""
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setStatusBarDarkTheme(this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, this.getColor(R.color.bg_ffffff))
        }

        canSlideFinish(true)
        initBinding()
        initView()
    }

    private fun initView() {
        binding.apply {
            tv_finish = tvPersonFinish
            edittext = etPerson
            back = icPersonBack
            tv_num = tvPersonNum

        }
        edittext.text = SpannableStringBuilder(slogan)
        tv_num.text = (15 - edittext.text.length).toString()
        tv_finish.setOnClickListener {
            var intent = Intent(this, MyinfoActivity::class.java)
//            var bundle = Bundle()
            var info = edittext.getText().toString()
            if (info.isNullOrEmpty())
                info = "未填写"
            intent.putExtra("personSign", info.trim())
            setResult(RESULT_OK, intent)
            finish()
        }

        back.setOnClickListener {
            finish()
        }

        edittext.addTextChangedListener(EditTextTextChangeListener({
            val num = 15 - it.length
            tv_num.text = num.toString()
        }
        ))
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_person_sign
    }
}
