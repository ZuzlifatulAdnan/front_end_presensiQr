package com.smakartika.absensiqr.ui.absen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.smakartika.absensiqr.data.model.JadwalPelajaran
import com.smakartika.absensiqr.data.repository.AbsenRepository
import com.smakartika.absensiqr.utils.Result
import kotlinx.coroutines.launch

class AbsenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AbsenRepository(application)

    private val _jadwalState = MutableLiveData<Result<List<JadwalPelajaran>>>()
    val jadwalState: LiveData<Result<List<JadwalPelajaran>>> = _jadwalState

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false
    private var currentSearchQuery: String? = null
    private var currentHari: String? = null
    private val allJadwal = mutableListOf<JadwalPelajaran>()

    init {
        loadJadwal(isRefreshing = true)
    }

    fun loadJadwal(isRefreshing: Boolean = false) {
        if (isLoading) return
        if (isRefreshing) {
            currentPage = 1
            isLastPage = false
            allJadwal.clear()
        }
        if (isLastPage) return

        viewModelScope.launch {
            isLoading = true
            if (currentPage == 1) {
                _jadwalState.value = Result.Loading
            }

            try {
                val response = repository.getJadwalAbsen(currentPage, currentSearchQuery, currentHari)
                if (response.isSuccessful && response.body() != null) {
                    val paginator = response.body()!!
                    isLastPage = paginator.currentPage >= paginator.lastPage
                    allJadwal.addAll(paginator.data)
                    _jadwalState.postValue(Result.Success(ArrayList(allJadwal))) // Kirim salinan list
                    if (!isLastPage) {
                        currentPage++
                    }
                } else {
                    _jadwalState.postValue(Result.Error("Gagal memuat jadwal: ${response.message()}"))
                }
            } catch (e: Exception) {
                _jadwalState.postValue(Result.Error("Terjadi kesalahan: ${e.message}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun applyFilter(searchQuery: String?, hari: String?) {
        currentSearchQuery = searchQuery
        currentHari = if (hari == "Semua Hari") null else hari
        loadJadwal(isRefreshing = true)
    }

    fun resetFilter() {
        applyFilter(null, null)
    }
}