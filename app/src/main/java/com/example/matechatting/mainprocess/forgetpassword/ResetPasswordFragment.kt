package com.example.matechatting.mainprocess.forgetpassword


import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import com.example.matechatting.databinding.FragmentResetPasswordBinding
import com.example.matechatting.listener.EditTextTextChangeListener
import com.example.matechatting.mainprocess.changepassword.ChangePasswordState
import com.example.matechatting.mainprocess.changepassword.ChangePasswordState.Companion.NEW_ERROR
import com.example.matechatting.mainprocess.changepassword.ChangePasswordState.Companion.NEW_NO6
import com.example.matechatting.mainprocess.changepassword.ChangePasswordState.Companion.NEW_NULL
import com.example.matechatting.mainprocess.changepassword.ChangePasswordState.Companion.NEW_OK
import com.example.matechatting.mainprocess.changepassword.ChangePasswordState.Companion.NEW_TOO_LONG
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.dialog.OKDialogUtil

class ResetPasswordFragment : BaseFragment() {
    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var newEdit: EditText
    private lateinit var newClear: ImageView
    private lateinit var newError: TextView
    private lateinit var newSee: ImageView
    private lateinit var againEdit: EditText
    private lateinit var againClear: ImageView
    private lateinit var againSee: ImageView
    private lateinit var againError: TextView
    private lateinit var overButton: Button
    private var newNotNull = false
    private var againNotNull = false
    private var newCanSee = false
    private var againCanSee = false
    private var account = ""
    private lateinit var viewModel: ResetPassViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        init()
        initEdit()
        initClear()
        initCanSeePasswordListener()
        initViewModel()
        initOverButton()
        return binding.root
    }

    private fun initViewModel() {
        val factory = InjectorUtils.provideResetPassViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(ResetPassViewModel::class.java)
    }

    override fun initView() {
        binding.apply {
            newEdit = resetNewEdit
            newClear = resetNewClear
            newError = resetNewError
            newSee = resetNewImage
            againEdit = resetAgainEdit
            againClear = resetAgainClear
            againSee = resetAgainImage
            againError = resetAgainError
            overButton = resetButton
        }
    }

    private fun initEdit() {
        newEdit.addTextChangedListener(
            EditTextTextChangeListener({
                if (it.isNotEmpty()) {
                    newClear.visibility = View.VISIBLE
                    newNotNull = true
                    viewModel.checkNewPassword(it.toString()) { state ->
                        checkNewPass(state)
                    }
                } else {
                    newClear.visibility = View.GONE
                    newNotNull = false
                }
                canClick()
            }, { s: CharSequence, i: Int, i1: Int, i2: Int ->
                newError.text = ""
            })
        )
        againEdit.addTextChangedListener(EditTextTextChangeListener({
            if (it.isNotEmpty()) {
                againClear.visibility = View.VISIBLE
                againNotNull = true
            } else {
                againClear.visibility = View.GONE
                againNotNull = false
            }
            canClick()
        }, { s: CharSequence, i: Int, i1: Int, i2: Int ->
            againError.text = ""
        }))
    }

    private fun checkNewPass(state: Int) {
        when (state) {
            NEW_NO6 -> newError.text = "至少6位密码"
            NEW_ERROR -> newError.text = "需包含数字，字母，符号中至少2种元素"
            NEW_NULL -> newError.text = "请输入新密码"
            NEW_TOO_LONG -> newError.text = "至多十六位"
            NEW_OK -> newError.text = ""
        }

    }

    private fun canClick() {
        if (newNotNull && againNotNull) {
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

    private fun initClear() {
        val emptyString = SpannableStringBuilder("")
        newClear.setOnClickListener {
            newEdit.text = emptyString
        }
        againClear.setOnClickListener {
            againEdit.text = emptyString
        }
    }

    private fun initCanSeePasswordListener() {
        newSee.setOnClickListener {
            if (!newCanSee) {
                newCanSee = true
                newEdit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                newCanSee = false
                newEdit.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        againSee.setOnClickListener {
            if (!againCanSee) {
                againCanSee = true
                againEdit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                againCanSee = false
                againEdit.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    /**
     * 设置@link[overButton]的点击事件
     * 未实现
     */
    private fun initOverButton() {
        overButton.setOnClickListener {
            viewModel.checkChangePassword(newEdit.text.toString(), againEdit.text.toString()) {
                when (it) {
                    NEW_NO6, NEW_ERROR, NEW_NULL, NEW_TOO_LONG -> checkNewPass(it)
                    ChangePasswordState.AGAIN_NULL -> againError.text = "请再次输入密码"
                    ChangePasswordState.AGAIN_ERROR -> againError.text = "两次密码不同,请再次输入"
                    ChangePasswordState.OK -> {
                        showDialog()
                    }
                }
            }
        }
    }

    private fun showDialog() {
        OKDialogUtil().initOKDialog(requireContext(), "修改成功") {
            val intent = Intent()
            intent.putExtra("account", ForgetPasswordActivity.account)
            val activity = (requireActivity() as ForgetPasswordActivity)
            activity.setResult(Activity.RESULT_OK,intent)
            ForgetPasswordActivity.token = ""
            ForgetPasswordActivity.account = ""
            activity.finish()
        }
    }


}
