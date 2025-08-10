package com.smakartika.absensiqr.data.repository

import android.content.Context
import com.smakartika.absensiqr.data.model.ChangePasswordRequest
import com.smakartika.absensiqr.data.remote.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

// Repository pattern untuk memisahkan sumber data dari ViewModel
class ProfileRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun getProfile() = apiService.getProfile()

    suspend fun getEditProfileData() = apiService.getEditProfileData()

    suspend fun updateProfile(
        fields: Map<String, RequestBody>,
        imagePart: MultipartBody.Part?
    ) = apiService.updateProfile(fields, imagePart)

    suspend fun changePassword(request: ChangePasswordRequest) = apiService.changePassword(request)
}