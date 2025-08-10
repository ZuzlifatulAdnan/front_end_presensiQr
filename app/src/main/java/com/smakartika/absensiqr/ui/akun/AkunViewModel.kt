package com.smakartika.absensiqr.ui.akun

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.smakartika.absensiqr.data.model.ChangePasswordRequest
import com.smakartika.absensiqr.data.model.Kelas
import com.smakartika.absensiqr.data.model.User
import com.smakartika.absensiqr.data.repository.ProfileRepository
import com.smakartika.absensiqr.utils.Result
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AkunViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)

    // ... (LiveData lainnya tetap sama) ...
    private val _profileState = MutableLiveData<Result<User>>()
    val profileState: LiveData<Result<User>> = _profileState

    private val _updateResult = MutableLiveData<Result<String>>()
    val updateResult: LiveData<Result<String>> = _updateResult

    private val _changePasswordResult = MutableLiveData<Result<String>>()
    val changePasswordResult: LiveData<Result<String>> = _changePasswordResult

    private val _kelasList = MutableLiveData<List<Kelas>>()
    val kelasList: LiveData<List<Kelas>> = _kelasList


    // Fungsi untuk menyalin file dari Uri ke cache aplikasi
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        // Buat file sementara di direktori cache
        val tempFile = File.createTempFile("upload_temp_", ".jpg", context.cacheDir)
        tempFile.deleteOnExit()

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null // Kembalikan null jika terjadi error
        }
        return tempFile
    }

    fun updateProfile(fields: Map<String, String>, imageUri: Uri?) {
        _updateResult.value = Result.Loading
        viewModelScope.launch {
            try {
                val requestBodyMap = fields.mapValues {
                    it.value.toRequestBody("text/plain".toMediaTypeOrNull())
                }

                var imagePart: MultipartBody.Part? = null
                imageUri?.let { uri ->
                    // Gunakan fungsi baru yang aman untuk mendapatkan file
                    getFileFromUri(getApplication(), uri)?.let { file ->
                        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
                    }
                }

                val response = repository.updateProfile(requestBodyMap, imagePart)
                if (response.isSuccessful && response.body() != null) {
                    _updateResult.postValue(Result.Success(response.body()!!.message))
                } else {
                    _updateResult.postValue(Result.Error("Gagal update: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _updateResult.postValue(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }

    // ... (Fungsi fetchProfile, fetchEditProfileData, changePassword tetap sama) ...
    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = Result.Loading
            try {
                val response = repository.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    _profileState.postValue(Result.Success(response.body()!!.data))
                } else {
                    _profileState.postValue(Result.Error("Gagal memuat profil: ${response.message()}"))
                }
            } catch (e: Exception) {
                _profileState.postValue(Result.Error("Terjadi kesalahan jaringan: ${e.message}"))
            }
        }
    }

    fun fetchEditProfileData() {
        viewModelScope.launch {
            try {
                val response = repository.getEditProfileData()
                if (response.isSuccessful && response.body() != null) {
                    _kelasList.postValue(response.body()!!.kelas)
                }
            } catch (_: Exception) { /* Biarkan dropdown kosong jika gagal */ }
        }
    }

    fun changePassword(request: com.smakartika.absensiqr.data.model.ChangePasswordRequest) {
        viewModelScope.launch {
            _changePasswordResult.value = Result.Loading
            try {
                val response = repository.changePassword(request)
                if (response.isSuccessful && response.body() != null) {
                    _changePasswordResult.postValue(Result.Success(response.body()!!.message))
                } else {
                    _changePasswordResult.postValue(Result.Error("Gagal: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _changePasswordResult.postValue(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }
}