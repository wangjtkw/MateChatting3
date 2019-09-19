package com.example.matechatting.mainprocess.forgetpassword


import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.matechatting.R
import com.example.matechatting.base.BaseFragment
import com.example.matechatting.databinding.FragmentFrogetPasswordBinding
import com.example.matechatting.listener.EditTextTextChangeListener
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.CODE_ERROR
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.CODE_NULL
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.ERROR
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.OK
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.PHONE_ERROR
import com.example.matechatting.mainprocess.bindphone.BindPhoneState.Companion.PHONE_NULL
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.NetworkState
import com.example.matechatting.utils.ToastUtilWarning
import com.example.matechatting.utils.isNetworkConnected

class ForgetPasswordFragment : BaseFragment() {
    private lateinit var binding: FragmentFrogetPasswordBinding
    private lateinit var phoneEdit: EditText
    private lateinit var phoneClear: ImageView
    private lateinit var phoneError: TextView
    private lateinit var codeEdit: EditText
    private lateinit var codeClear: ImageView
    private lateinit var sendButton: Button
    private lateinit var codeError: TextView
    private lateinit var nextButton: Button
    private var phoneNotNull = false
    private var codeNotNull = false
    private var sendCanClick = false
    private var isCountdown = false
    private var isFinish = false

    private lateinit var viewModel: ForgetPasswordViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_froget_password, container, false)
        init()
        initViewModel()
        initEditText()
        initSendButton()
        initClear()
        initNextButton()
        return binding.root
    }

    private fun initViewModel() {
        val factory = InjectorUtils.provideForgetPasswordViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(ForgetPasswordViewModel::class.java)
    }

    /**
     * 初始化视图
     */
    override fun initView() {
        binding.apply {
            phoneEdit = forgetPhoneEdit
            phoneClear = forgetPhoneClear
            phoneError = forgetPhoneError
            codeEdit = forgetCheckCodeEdit
            codeClear = forgetCheckCodeClear
            codeError = forgetCheckCodeError
            sendButton = forgetSendMessage
            nextButton = forgetNextButton
        }
    }

    /**
     * 设置EditText @link [phoneEdit] [codeEdit]的输入监听
     * 设置当输入内容时 显示@link [phoneClear] [codeClear] 可见
     * 设置@link [phoneNotNull] [codeNotNull] 来监听输入是否为空
     * 设置@link [phoneError] [codeError] 内容清空
     * 调用@link [canClick] 来切换@link [nextButton] 背景
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
//                    viewModel.checkCode(phoneEdit.text.toString(), codeEdit.text.toString()) { result ->
//                        when (result) {
//                            PHONE_NULL -> phoneError.text = "请先输入手机号"
//                            CODE_NULL -> codeError.text = "请输入验证码"
//                            CODE_ERROR -> codeError.text = "请输入六位数字验证码"
//                            PHONE_ERROR -> phoneError.text = "手机号码输入错误"
//                            ERROR -> codeError.text = "验证错误"
//                            OK -> {
//                                replace()
//                            }
//                        }
//                    }
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

    private fun checkPhone(phoneNum: String) {
        val regexPhone = Regex("^[1]([3-9])[0-9]{9}\$")
        val resultPhone = regexPhone.find(phoneNum)
        if (resultPhone == null) {
            phoneError.text = "手机号码输入错误"
        } else {
            sendCanClick = true
            sendButtonCanClick()
        }
    }

    /**
     * 根据输入是否为空设置@link[nextButton] 是否可点击
     * 调用点击后的操作（未实现）
     */
    private fun canClick() {
        if (phoneNotNull && codeNotNull) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                nextButton.background = this.resources.getDrawable(R.drawable.shape_bt_enable, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                nextButton.background = this.resources.getDrawable(R.drawable.shape_bt_enable)
            }
            nextButton.isEnabled = true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                nextButton.background = this.resources.getDrawable(R.drawable.shape_bt_disable, null)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                nextButton.background = this.resources.getDrawable(R.drawable.shape_bt_disable)
            }
            nextButton.isEnabled = false
        }
    }

    /**
     * 设置@link[sendButton] 的点击事件
     * 调用@link[setCountdown] 进行倒计时
     */
    private fun initSendButton() {
        sendButton.setOnClickListener {
            viewModel.sendMessage(phoneEdit.text.toString()) {
                when (it) {
                    ERROR -> phoneError.text = "当前手机号未绑定账号"
                    PHONE_ERROR -> phoneError.text = "手机号码输入错误"
                    OK -> setCountdown()
                }
            }

        }
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

    /**
     * 设置倒计时
     * 有bug 切换activity后倒计时会清空
     */
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
                if (!isFinish){
                    sendButtonCanClick()
                }
            }
        }.start()
    }

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

    /**
     * 设置@link[nextButton] 的点击事件
     * 未实现
     */
    private fun initNextButton() {
        nextButton.setOnClickListener {
            if (isNetworkConnected(requireContext()) == NetworkState.NONE){
                ToastUtilWarning().setToast(requireContext(),"当前网络不可用")
            }else{
                viewModel.checkCode(phoneEdit.text.toString(), codeEdit.text.toString()) { result ->
                    when (result) {
                        PHONE_NULL -> phoneError.text = "请先输入手机号"
                        CODE_NULL -> codeError.text = "请输入验证码"
                        CODE_ERROR -> codeError.text = "请输入六位数字验证码"
                        PHONE_ERROR -> phoneError.text = "手机号码输入错误"
                        ERROR -> codeError.text = "验证错误"
                        OK -> {
                            replace()
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        isFinish = true
    }

    private fun replace(){
        (requireActivity() as ForgetPasswordActivity).replaceFragment(ResetPasswordFragment(), "重置密码")
    }

}
