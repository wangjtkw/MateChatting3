package com.example.matechatting.mainprocess.infodetail

import android.util.Log
import android.widget.ImageView
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.matechatting.BASE_URL
import com.example.matechatting.MORE_BASE
import com.example.matechatting.PATH
import com.example.matechatting.R
import com.example.matechatting.bean.UserBean

class InfoDetailViewModel(private val repository: InfoDetailRepository) : ViewModel() {
    val detailName = ObservableField("未填写")
    val detailMajor = ObservableField("未填写")
    val detailGraduate = ObservableField("未填写")
    val detailCompany = ObservableField("未填写")
    val detailJob = ObservableField("未填写")
    val detailDirection = ObservableField("未填写")
    val detailQQ = ObservableField("未填写")
    val detailWeixin = ObservableField("未填写")
    val detailEmile = ObservableField("未填写")
    val detailCity = ObservableField("未填写")
    val detailSlogan = ObservableField("未填写")
    val defaltString = "未填写"
    val defaultSlogan = "快乐生活每一天"

    fun getDetail(id: Int, headImageView: ImageView, callback: (UserBean) -> Unit) {
        repository.getDetail(id) {
            callback(it)
            Log.d("aaa", "当前user $it ")
            it.apply {
                if (name.isEmpty()) {
                    detailName.set(defaltString)
                } else {
                    detailName.set(name)
                }
                if (majorName.isEmpty()) {
                    detailMajor.set(defaltString)
                } else {
                    detailMajor.set(majorName)
                }
                if (graduation.isEmpty()) {
                    detailGraduate.set(defaltString)
                } else {
                    detailGraduate.set(graduation)
                }
                if (company.isEmpty()) {
                    detailCompany.set(defaltString)
                } else {
                    detailCompany.set(company)
                }
                if (job.isEmpty()) {
                    detailJob.set(defaltString)
                } else {
                    detailJob.set(job)
                }
                if (direction.isEmpty()) {
                    detailDirection.set(defaltString)
                } else {
                    detailDirection.set(direction)
                }
                if (qqAccount.toString().isEmpty() || qqAccount == 0L) {
                    detailQQ.set(defaltString)
                } else {
                    detailQQ.set(qqAccount.toString())
                }
                if (wechatAccount.isEmpty()) {
                    detailWeixin.set(defaltString)
                } else {
                    detailWeixin.set(wechatAccount)
                }
                if (email.isEmpty()) {
                    detailEmile.set(defaltString)
                } else {
                    detailEmile.set(email)
                }
                if (city.isEmpty()) {
                    detailCity.set(defaltString)
                } else {
                    detailCity.set(city)
                }
                if (slogan.isEmpty()) {
                    detailSlogan.set(defaultSlogan)
                } else {
                    detailSlogan.set(slogan)
                }
                if (!headImage.isNullOrEmpty()) {
                    setHeadImage(headImageView, headImage!!)
                }
            }
        }
    }

    private fun setHeadImage(headImage: ImageView, imageUrl: String) {
        val sb = StringBuilder()
        sb.append(BASE_URL)
            .append(MORE_BASE)
            .append(PATH)
            .append(imageUrl)
        Glide.with(headImage.context)
            .load(sb.toString())
            .error(R.drawable.ic_head)
            .into(headImage)
    }


}