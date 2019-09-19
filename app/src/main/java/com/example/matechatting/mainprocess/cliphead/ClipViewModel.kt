package com.example.matechatting.mainprocess.cliphead

import androidx.lifecycle.ViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ClipViewModel(private val repository: ClipRepository) : ViewModel() {

    fun postImage(file: File, callback: () -> Unit) {
        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        val body = MultipartBody.Part.createFormData("profile_photo", file.name, requestBody)
        repository.postImage(body)
        val path = file.absolutePath
        saveHeadInDB(path,callback)
    }

    private fun saveHeadInDB(filePath: String,callback: () -> Unit) {
        repository.saveInDB(filePath,callback)
    }
}