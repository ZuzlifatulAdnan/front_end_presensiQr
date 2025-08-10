package com.smakartika.absensiqr.ui.absen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smakartika.absensiqr.data.model.AbsenScanRequest
import com.smakartika.absensiqr.data.model.KelasDetail
import com.smakartika.absensiqr.data.repository.AbsenRepository
import com.smakartika.absensiqr.utils.Result
import kotlinx.coroutines.launch

class AbsenScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AbsenRepository(application)

    private val _scanResult = MutableLiveData<Result<String>>()
    val scanResult: LiveData<Result<String>> = _scanResult

    private val _kelasData = MutableLiveData<Result<KelasDetail>>()
    val kelasData: LiveData<Result<KelasDetail>> = _kelasData

    fun fetchScanFormData() {
        viewModelScope.launch {
            _kelasData.value = Result.Loading
            try {
                val response = repository.getScanFormData()
                if (response.isSuccessful && response.body() != null) {
                    _kelasData.postValue(Result.Success(response.body()!!.data.kelas))
                } else {
                    _kelasData.postValue(Result.Error("Gagal memuat data lokasi kelas"))
                }
            } catch (e: Exception) {
                _kelasData.postValue(Result.Error("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }

    fun submitAbsen(tokenQr: String, lat: Double, long: Double) {
        viewModelScope.launch {
            _scanResult.value = Result.Loading
            val request = AbsenScanRequest(tokenQr, lat, long)
            try {
                val response = repository.submitScan(request)
                if (response.isSuccessful && response.body() != null) {
                    _scanResult.postValue(Result.Success(response.body()!!.message))
                } else {
                    // Coba parsing error body dari JSON untuk pesan yang lebih spesifik
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        try {
                            val type = object : TypeToken<Map<String, String>>() {}.type
                            val errorResponse: Map<String, String> = Gson().fromJson(errorBody, type)
                            _scanResult.postValue(Result.Error(errorResponse["message"] ?: "Terjadi kesalahan"))
                        } catch (e: Exception) {
                            _scanResult.postValue(Result.Error(errorBody))
                        }
                    } else {
                        _scanResult.postValue(Result.Error("Terjadi kesalahan"))
                    }
                }
            } catch (e: Exception) {
                _scanResult.postValue(Result.Error("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
}
