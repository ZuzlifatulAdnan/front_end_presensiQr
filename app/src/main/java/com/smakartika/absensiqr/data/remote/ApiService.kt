package com.smakartika.absensiqr.data.remote

import com.smakartika.absensiqr.data.model.BerandaResponse
import com.smakartika.absensiqr.data.model.LoginRequest
import com.smakartika.absensiqr.data.model.LoginResponse
import retrofit2.http.Body
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/login")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<LoginResponse>
    // Tambahkan fungsi ini
    @GET("api/beranda")
    suspend fun getBeranda(
        @Query("page") page: Int,
        @Query("hari") hari: String? // Filter hari (bisa null)
    ): Response<BerandaResponse>
}