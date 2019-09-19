package com.example.matechatting.mainprocess.bindphone

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.example.matechatting.R
import com.example.matechatting.base.BaseActivity
import com.example.matechatting.databinding.ActivityBindPhoneBinding
import com.example.matechatting.listener.EditTextTextChangeListener
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.CODE_ERROR
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.OK
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.PHONE_ERROR
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.PHONE_NULL
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.dialog.OKDialogUtil
import com.example.matechatting.utils.statusbar.StatusBarUtil

class BindPhoneActivity : BaseActivity<ActivityBindPhoneBinding>() {
    private lateinit var back: FrameLayout
    private lateinit var phoneEdit: EditText
    private lateinit var phoneClear: ImageView
    private lateinit var phoneError: TextView
    private lateinit var codeEdit: EditText
    private lateinit var codeClear: ImageView
    private lateinit var codeError: TextView
    private lateinit var sendButton: Button
    private lateinit var overButton: Button
    private var phoneNotNull = false
    private var codeNotNull = false
    private var sendCanClick = false
    private var isCountdown = false

    private lateinit var viewModel: BindPhoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setStatusBarDarkTheme(this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(this, this.getColor(R.color.bg_ffffff))
        }
        canSlideFinish(true)
        initBinding()
        init()
        initView()
        initEditText()
        initSendButton()
        initClear()
        initBack()
        initOverButton()
    }

    private fun init() {
        val factory = InjectorUtils.provideBindPhoneViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(BindPhoneViewModel::class.java)
    }

    private fun initView() {
        binding.apply {
            back = bindPhoneBack
            phoneEdit = bindPhoneEdittext
            phoneClear = bindPhoneClear
            phoneError = bindPhoneError
            codeEdit = bindCodeEdittext
            codeClear = bindCodeClear
            codeError = bindCodeError
            sendButton = bindSendMessage
            overButton = bindPhoneButton

        }
    }

    /**
     * 返回按钮的点击事件
     */
    private fun initBack() {
        back.setOnClickListener {
            finish()
        }
    }

    /**
     * 设置@link [phoneEdit] [codeEdit] 的监听事件
     * 当手机号输入 @link [phoneEdit] 达到11位时，调用@link [checkPhone]自动正则检查手机号输入格式是否正确
     * 设置@link [sendButton] 发送短信按钮是否可点击
     */
    private fun initEditText() {
        phoneEdit.addTextChangedListener(
            EditTextTextChangeListener({
                if (it.isNotEmpty()) {
                    phoneClear.visibility = View.VISIBLE
                    phoneNotNull = true
                } else {
                    phoneClear.visibility = View.GONE
                    phoneNotNull = false
                }
                sendCanClick = false
                if (it.length == 11) {
                    checkPhone(it.toString())
                }
                sendButtonCanClick()
                canClick()
            }, { s: CharSequence, i: Int, i1: Int, i2: Int ->
                phoneError.text = ""
            })
        )
        codeEdit.addTextChangedListener(EditTextTextChangeListener({
            if (it.isNotEmpty()) {
                codeClear.visibility = View.VISIBLE

                if (it.length == 6) {
                    codeNotNull = true

                }
            } else {
                codeClear.visibility = View.GONE
                codeNotNull = false
            }
            canClick()
        }, { s: CharSequence, i: Int, i1: Int, i2: Int ->
            codeError.text = ""
        }))
    }

    private fun showDialog() {
        OKDialogUtil().initOKDialog(this, "绑定成功") {
            finish()
        }
    }

    /**
     * 检查手机号格式是否输入正确
     */
    private fun checkPhone(phoneNum: String) {
        val regexPhone = Regex("^[1]([3-9])[0-9]{9}\$")
        val resultPhone = regexPhone.find(phoneNum)
        if (resultPhone == null) {
            phoneError.text = "手机号码输入错误"
        } else {
            sendCanClick = true
        }
    }

    /**
     * 发送短信按钮是否可点击
     * 并实现替换背景
     */
    private fun sendButtonCanClick() {
        if (sendCanClick && !isCountdown) {
            sendButton.isEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sendButton.background = this.resources.getDrawable(R.drawable.shape_circle_corner_line, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sendButton.background = this.resources.getDrawable(R.drawable.shape_circle_corner_line)
            }
        } else {
            sendButton.isEnabled = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sendButton.background = this.resources.getDrawable(R.drawable.shape_circle_corner_line_disable, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sendButton.background = this.resources.getDrawable(R.drawable.shape_circle_corner_line_disable)
            }
        }
    }


    private fun canClick() {
        if (phoneNotNull && codeNotNull) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                overButton.background = this.resources.getDrawable(R.drawable.shape_bt_enable, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                overButton.background = this.resources.getDrawable(R.drawable.shape_bt_enable)
            }
            overButton.isEnabled = true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                overButton.background = this.resources.getDrawable(R.drawable.shape_bt_disable, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                overButton.background = this.resources.getDrawable(R.drawable.shape_bt_disable)
            }
            overButton.isEnabled = false
        }
    }

    private fun initSendButton() {
        sendButton.setOnClickListener {
            val phoneNum = phoneEdit.text
            if (phoneNum != null) {
                viewModel.sendMessage(phoneNum.toString()) {
                    Log.d("aaa", it.toString())
                    when (it) {
                        PHONE_ERROR -> {
                            phoneError.text = "手机号码输入错误"
                        }
                        OK -> {
                            setCountdown()
                        }
                    }
                }
            }
        }
    }

    private fun initOverButton() {
        overButton.setOnClickListener {
            viewModel.checkCode(phoneEdit.text.toString(), codeEdit.text.toString()) { result ->
                when (result) {
                    PHONE_NULL -> phoneError.text = "请先输入手机号"
                    PHONE_ERROR -> phoneError.text = "手机号码输入错误"
                    CODE_ERROR -> codeError.text = "验证码输入错误"
                    OK -> {
                        showDialog()
                    }
                }
            }
        }
    }

    private fun setCountdown() {
        object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(p0: Long) {
                isCountdown = true
                val s = p0.toInt() / 1000
                val str = "$s 秒后可再次发送"
                sendButton.text = str
            }

            override fun onFinish() {
                sendButton.text = "获取验证码"
                isCountdown = false
                sendButtonCanClick()
            }
        }.start()
    }

    private fun initClear() {
        val emptyString = SpannableStringBuilder("")
        phoneClear.setOnClickListener {
            phoneEdit.text = emptyString
        }
        codeClear.setOnClickListener {
            codeEdit.text = emptyString
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_bind_phone
    }
}
