package com.smakartika.absensiqr.data.repository

import android.content.Context
import com.smakartika.absensiqr.data.remote.ApiClient
import com.smakartika.absensiqr.data.remote.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class IzinRepository(context: Context) {
    private val apiService: ApiService = ApiClient.getInstance(context)

    suspend fun getIzinList(page: Int, tanggal: String?, search: String?) =
        apiService.getIzinList(page, tanggal, search)

    suspend fun storeIzin(fields: Map<String, RequestBody>, imagePart: MultipartBody.Part) =
        apiService.storeIzin(fields, imagePart)

    suspend fun getIzinDetail(izinId: Int) = apiService.getIzinDetail(izinId)
}