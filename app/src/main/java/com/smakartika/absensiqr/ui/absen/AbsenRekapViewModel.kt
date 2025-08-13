package com.smakartika.absensiqr.ui.absen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.smakartika.absensiqr.data.model.RekapAbsenResponse
import com.smakartika.absensiqr.data.repository.AbsenRepository // Asumsi repository ini ada
import com.smakartika.absensiqr.utils.Result
import kotlinx.coroutines.launch

class AbsenRekapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AbsenRepository(application)

    private val _rekapState = MutableLiveData<Result<RekapAbsenResponse>>()
    val rekapState: LiveData<Result<RekapAbsenResponse>> = _rekapState

    fun fetchRekapAbsen(jadwalId: Int) {
        viewModelScope.launch {
            _rekapState.value = Result.Loading
            try {
                val response = repository.getRekapAbsen(jadwalId) // Anda perlu menambahkan fungsi ini di repository
                if (response.isSuccessful && response.body() != null) {
                    _rekapState.postValue(Result.Success(response.body()!!))
                } else {
                    _rekapState.postValue(Result.Error("Gagal memuat rekap: ${response.message()}"))
                }
            } catch (e: Exception) {
                _rekapState.postValue(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }
}
