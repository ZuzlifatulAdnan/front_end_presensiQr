package com.smakartika.absensiqr.data.repository

import android.content.Context
import com.smakartika.absensiqr.data.remote.ApiClient

class BerandaRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun getBeranda(page: Int, hari: String?) = apiService.getBeranda(page, hari)
}