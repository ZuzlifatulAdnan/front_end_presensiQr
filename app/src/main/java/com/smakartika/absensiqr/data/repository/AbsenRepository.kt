package com.smakartika.absensiqr.data.repository

import android.content.Context
import com.smakartika.absensiqr.data.model.AbsenScanRequest
import com.smakartika.absensiqr.data.remote.ApiClient
import com.smakartika.absensiqr.data.remote.ApiService

class AbsenRepository(context: Context) {
    private val apiService: ApiService = ApiClient.getInstance(context)

    suspend fun getJadwalAbsen(page: Int, searchQuery: String?, hari: String?) =
        apiService.getJadwalAbsen(page, searchQuery, hari)
    suspend fun getScanFormData() = apiService.getScanFormData()
    suspend fun submitScan(request: AbsenScanRequest) = apiService.submitScan(request)
}