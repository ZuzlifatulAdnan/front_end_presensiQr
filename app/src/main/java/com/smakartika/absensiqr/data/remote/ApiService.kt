package com.smakartika.absensiqr.data.remote

import com.smakartika.absensiqr.data.model.AbsenScanRequest
import com.smakartika.absensiqr.data.model.AbsenScanResponse
import com.smakartika.absensiqr.data.model.BerandaResponse
import com.smakartika.absensiqr.data.model.ChangePasswordRequest
import com.smakartika.absensiqr.data.model.ChangePasswordResponse
import com.smakartika.absensiqr.data.model.EditProfileDataResponse
import com.smakartika.absensiqr.data.model.JadwalPaginator
import com.smakartika.absensiqr.data.model.Kelas
import com.smakartika.absensiqr.data.model.LoginRequest
import com.smakartika.absensiqr.data.model.LoginResponse
import com.smakartika.absensiqr.data.model.ProfileResponse
import com.smakartika.absensiqr.data.model.RekapAbsenResponse
import com.smakartika.absensiqr.data.model.ScanFormDataResponse
import com.smakartika.absensiqr.data.model.UpdateProfileResponse
import retrofit2.http.Body
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
//    login
    @POST("api/login")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<LoginResponse>
    // beranda
    @GET("api/beranda")
    suspend fun getBeranda(
        @Query("page") page: Int,
        @Query("hari") hari: String? // Filter hari (bisa null)
    ): Response<BerandaResponse>
    @GET("api/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @GET("api/profile/edit")
    suspend fun getEditProfileData(): Response<EditProfileDataResponse>

    @Multipart
    @POST("api/profile/update")
    suspend fun updateProfile(
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part foto: MultipartBody.Part? // Nama field di API adalah 'foto'
    ): Response<UpdateProfileResponse>

    @POST("api/profile/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>
    //    absen
    @GET("api/absen")
    suspend fun getJadwalAbsen(
        @Query("page") page: Int,
        @Query("q") searchQuery: String?, // untuk cari guru atau mapel
        @Query("hari") hari: String?
    ): Response<JadwalPaginator>
    @GET("api/absen/scan-form")
    suspend fun getScanFormData(): Response<ScanFormDataResponse>
    @POST("api/absen/scan")
    suspend fun submitScan(@Body request: AbsenScanRequest): Response<AbsenScanResponse>
    @GET("api/absen/{id}/rekap")
    suspend fun getRekapAbsen(@Path("id") jadwalId: Int): Response<RekapAbsenResponse>
}