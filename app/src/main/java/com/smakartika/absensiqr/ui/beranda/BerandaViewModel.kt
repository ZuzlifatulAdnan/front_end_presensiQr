package com.smakartika.absensiqr.ui.beranda

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.smakartika.absensiqr.data.model.BerandaResponse
import com.smakartika.absensiqr.data.repository.BerandaRepository
import kotlinx.coroutines.launch

sealed class BerandaState {
    data class Success(val data: BerandaResponse) : BerandaState()
    data class Error(val message: String) : BerandaState()
    object Loading : BerandaState()
}

class BerandaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BerandaRepository(application)

    private val _berandaState = MutableLiveData<BerandaState>()
    val berandaState: LiveData<BerandaState> = _berandaState

    private var currentPage = 1
    private var currentHariFilter: String? = null

    init {
        fetchBerandaData()
    }

    fun fetchBerandaData(page: Int = 1, hari: String? = currentHariFilter) {
        if (page == currentPage && hari == currentHariFilter && berandaState.value is BerandaState.Success) {
            return // Hindari reload jika data dan filter sama
        }

        currentPage = page
        currentHariFilter = hari
        _berandaState.value = BerandaState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getBeranda(page, hari)
                if (response.isSuccessful && response.body() != null) {
                    _berandaState.postValue(BerandaState.Success(response.body()!!))
                } else {
                    _berandaState.postValue(BerandaState.Error("Gagal memuat data: ${response.code()}"))
                }
            } catch (e: Exception) {
                _berandaState.postValue(BerandaState.Error("Terjadi Kesalahan Jaringan."))
            }
        }
    }
}