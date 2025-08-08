package com.smakartika.absensiqr.data.repository

import android.content.Context
import com.smakartika.absensiqr.data.model.LoginRequest
import com.smakartika.absensiqr.data.remote.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun login(request: LoginRequest) = apiService.loginAdmin(request)

}