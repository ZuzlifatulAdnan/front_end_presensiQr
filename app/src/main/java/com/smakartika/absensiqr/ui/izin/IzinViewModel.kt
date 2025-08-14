package com.smakartika.absensiqr.ui.izin

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.smakartika.absensiqr.data.model.Izin
import com.smakartika.absensiqr.data.repository.IzinRepository
import com.smakartika.absensiqr.utils.Result
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class IzinViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = IzinRepository(application)

    private val _izinListState = MutableLiveData<Result<List<Izin>>>()
    val izinListState: LiveData<Result<List<Izin>>> = _izinListState

    private val _izinDetailState = MutableLiveData<Result<Izin>>()
    val izinDetailState: LiveData<Result<Izin>> = _izinDetailState

    private val _storeIzinResult = MutableLiveData<Result<String>>()
    val storeIzinResult: LiveData<Result<String>> = _storeIzinResult

    fun fetchIzinList(page: Int, tanggal: String?, search: String?) {
        viewModelScope.launch {
            _izinListState.value = Result.Loading
            try {
                val response = repository.getIzinList(page, tanggal, search)
                if (response.isSuccessful && response.body() != null) {
                    _izinListState.postValue(Result.Success(response.body()!!.data.data))
                } else {
                    _izinListState.postValue(Result.Error("Gagal memuat data: ${response.message()}"))
                }
            } catch (e: Exception) {
                _izinListState.postValue(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }

    fun fetchIzinDetail(izinId: Int) {
        viewModelScope.launch {
            _izinDetailState.value = Result.Loading
            try {
                val response = repository.getIzinDetail(izinId)
                if (response.isSuccessful && response.body() != null) {
                    _izinDetailState.postValue(Result.Success(response.body()!!.data))
                } else {
                    _izinDetailState.postValue(Result.Error("Gagal memuat detail: ${response.message()}"))
                }
            } catch (e: Exception) {
                _izinDetailState.postValue(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }

    // PERBAIKAN: Fungsi ini sekarang akan mengompres gambar
    private fun getCompressedFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("bukti_surat_compressed_", ".jpg", context.cacheDir)
        tempFile.deleteOnExit()

        try {
            // Buka stream dari URI
            contentResolver.openInputStream(uri)?.use { inputStream ->
                // Decode stream menjadi Bitmap
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Kompres Bitmap dan tulis ke file sementara
                FileOutputStream(tempFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Kualitas 80%
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return tempFile
    }

    fun storeIzin(tanggal: String, alasan: String, imageUri: Uri) {
        viewModelScope.launch {
            _storeIzinResult.value = Result.Loading

            val context = getApplication<Application>().applicationContext
            // PERBAIKAN: Panggil fungsi kompresi yang baru
            val imageFile = getCompressedFileFromUri(context, imageUri)
            val mimeType = "image/jpeg" // Karena kita kompres ke JPEG

            if (imageFile == null) {
                _storeIzinResult.postValue(Result.Error("Gagal memproses file gambar."))
                return@launch
            }

            try {
                val fields = mapOf(
                    "tanggal_izin" to tanggal.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "alasan" to alasan.toRequestBody("text/plain".toMediaTypeOrNull())
                )
                val requestFile = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("bukti_surat", imageFile.name, requestFile)

                val response = repository.storeIzin(fields, imagePart)
                if (response.isSuccessful && response.body() != null) {
                    _storeIzinResult.postValue(Result.Success(response.body()!!.message))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val json = JSONObject(errorBody)
                        val errors = json.getJSONObject("errors")
                        val buktiSuratErrors = errors.getJSONArray("bukti_surat")
                        buktiSuratErrors.getString(0)
                    } catch (e: Exception) {
                        "Gagal mengirim izin." // Fallback message
                    }
                    _storeIzinResult.postValue(Result.Error(errorMessage))
                }
            } catch (e: Exception) {
                _storeIzinResult.postValue(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }
}