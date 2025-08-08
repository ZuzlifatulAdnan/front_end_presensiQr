package com.smakartika.absensiqr.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.smakartika.absensiqr.data.model.LoginRequest
import com.smakartika.absensiqr.data.model.LoginResponse
import com.smakartika.absensiqr.data.repository.AuthRepository
import kotlinx.coroutines.launch

// Kelas helper untuk merepresentasikan state UI yang berbeda
sealed class LoginResult {
    data class Success(val data: LoginResponse) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object Loading : LoginResult()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    // Inisialisasi repository, memberikan context dari application
    private val repository = AuthRepository(application)

    // LiveData internal yang bisa diubah hanya oleh ViewModel ini
    private val _loginResult = MutableLiveData<LoginResult>()
    // LiveData publik yang hanya bisa diamati (read-only) oleh Activity
    val loginResult: LiveData<LoginResult> = _loginResult

    fun executeLogin(email: String, password: String) {
        // 1. Set state ke Loading, agar UI menampilkan progress bar
        _loginResult.value = LoginResult.Loading

        // 2. Jalankan proses di background thread menggunakan coroutine
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                // 3. Panggil fungsi login dari repository
                val response = repository.login(request)

                // 4. Cek apakah response dari server sukses dan memiliki body
                if (response.isSuccessful && response.body() != null) {
                    // Jika sukses, kirim data ke UI melalui state Success
                    _loginResult.postValue(LoginResult.Success(response.body()!!))
                } else {
                    // Jika gagal, kirim pesan error dari server
                    _loginResult.postValue(LoginResult.Error("Login Gagal: ${response.message()}"))
                }
            } catch (e: Exception) {
                // 5. Tangani jika ada error koneksi atau error lainnya
                _loginResult.postValue(LoginResult.Error("Terjadi Kesalahan: ${e.message}"))
            }
        }
    }
}