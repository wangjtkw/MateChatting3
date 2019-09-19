package com.example.matechatting.mainprocess.mine


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.matechatting.ALBUM_REQUEST_CODE
import com.example.matechatting.LOGIN_REQUEST_CODE
import com.example.matechatting.R
import com.example.matechatting.base.PermissionFragment
import com.example.matechatting.databinding.FragmentMineBinding
import com.example.matechatting.mainprocess.album.AlbumActivity
import com.example.matechatting.mainprocess.bindphone.BindPhoneActivity
import com.example.matechatting.mainprocess.changepassword.ChangePasswordActivity
import com.example.matechatting.mainprocess.login.LoginActivity
import com.example.matechatting.mainprocess.myinfo.MyinfoActivity
import com.example.matechatting.utils.InjectorUtils
import com.example.matechatting.utils.dialog.AccessPermissionDialogUtil
import com.example.matechatting.utils.dialog.ChooseHeadImageDialogUtil

class MineFragment : PermissionFragment() {
    private lateinit var binding: FragmentMineBinding
    private lateinit var headLayout: ConstraintLayout
    private lateinit var headImage: ImageView
    private lateinit var headName: TextView
    private lateinit var headText: TextView
    private lateinit var functionLayout: ConstraintLayout
    private lateinit var changePassword: ConstraintLayout
    private lateinit var myInformation: ConstraintLayout
    private lateinit var bindPhone: ConstraintLayout
    private lateinit var chooseHeadImageCallback: () -> Unit
    private lateinit var viewModel: MineViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false)
        return binding.root
    }

    override fun initView() {
        binding.apply {
            headLayout = mineHeadLayout
            headImage = mineHeadImage
            headName = mineHeadName
            headText = mineHeadText
            functionLayout = mineFunctionLayout
            changePassword = mineChangePasswordLayout
            myInformation = mineMyInformationLayout
            bindPhone = mineBindPhoneLayout
        }
        val factory = InjectorUtils.provideMineViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(MineViewModel::class.java)
        binding.viewmodel = viewModel
    }

    override fun onResume() {
        super.onResume()
        Log.d("aaa","mine onResume")
        init()
    }

    private fun transferActivity(intent: Intent, requestCode: Int) {
        requireActivity().startActivityForResult(intent, requestCode)
    }

    /**
     * 处理登陆后的点击事件以及登陆后的逻辑
     */
    override fun initLogin() {
        viewModel.getMine{
            setHeadImage(it)
        }
        headImage.isEnabled = true
        changePassword.isEnabled = true
        myInformation.isEnabled = true
        bindPhone.isEnabled = true
        headLayout.isEnabled = true
        functionLayout.isEnabled = false
        val intentToChangePassword = Intent(requireActivity(), ChangePasswordActivity::class.java)
        changePassword.setOnClickListener {
            transferActivity(intentToChangePassword, 0x999)
        }
        val intentToMyInfo = Intent(requireActivity(), MyinfoActivity::class.java)
        myInformation.setOnClickListener {
            transferActivity(intentToMyInfo, 0x999)
        }
        headLayout.setOnClickListener {
            transferActivity(intentToMyInfo, 0x999)
        }
        val intentToBindPhone = Intent(requireActivity(), BindPhoneActivity::class.java)
        bindPhone.setOnClickListener {
            transferActivity(intentToBindPhone, 0x999)
        }
        val chooseHeadImageUtil = ChooseHeadImageDialogUtil()
        initChooseHeadImageCallback()
        headImage.setOnClickListener {
            chooseHeadImageUtil.initChooseHeadImageDialog(requireContext(), chooseHeadImageCallback)
        }
        val intent = Intent(requireActivity(), MyinfoActivity::class.java)
        headLayout.setOnClickListener {
            transferActivity(intent, 0x999)
        }
    }

    private fun setHeadImage(imageUrl:String){
        val end = imageUrl.endsWith(".jpg")
        if (end){
            val bitmap = BitmapFactory.decodeFile(imageUrl)
            headImage.setImageBitmap(bitmap)
            Log.d("aaa","加载本地图片")
        }else{
            Glide.with(headImage.context)
                .load(imageUrl)
                .error(R.drawable.ic_head)
                .into(headImage)
        }
    }

    @SuppressLint("InlinedApi")
    private fun initChooseHeadImageCallback() {
        chooseHeadImageCallback = {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ALBUM_REQUEST_CODE && data != null) {
            val path = data.getStringExtra("image_path")
            if (!path.isNullOrEmpty()) {
                val bitmap = BitmapFactory.decodeFile(path)
                headImage.setImageBitmap(bitmap)
//                Glide.with(this).load(uri).into(headImage)
            }
        }
    }

    override fun showDialogTipUserGoToAppSetting(permission: String) {
        if (permission == Manifest.permission.READ_EXTERNAL_STORAGE) {
            val accessPermissionDialogUtil = AccessPermissionDialogUtil()
            accessPermissionDialogUtil.initAccessPermissionDialog(requireContext(), {
                gotoAppSetting()
            }, {}).show()
        }
    }

    override fun doOnGetPermission() {
        val intent = Intent(requireActivity(), AlbumActivity::class.java)
        transferActivity(intent, ALBUM_REQUEST_CODE)
    }

    /**
     * 处理未登陆时的点击事件以及未登陆时的逻辑
     */
    override fun initNotLogin() {
        Log.d("aaa", "initNotLogin")
        headImage.isEnabled = false
        changePassword.isEnabled = false
        myInformation.isEnabled = false
        bindPhone.isEnabled = false
        headLayout.isEnabled = true
        functionLayout.isEnabled = true
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        headLayout.setOnClickListener {
            transferActivity(intent, LOGIN_REQUEST_CODE)
        }
        functionLayout.setOnClickListener {
            transferActivity(intent, LOGIN_REQUEST_CODE)
        }
        headName.text = "未登录"
        headText.text = "快乐生活每一天"
    }

}
