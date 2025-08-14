package com.smakartika.absensiqr.data.model

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Wrapper untuk response GET /api/izin (paginasi)
@Parcelize
data class IzinPaginator(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("data") val data: List<Izin>,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("total") val total: Int
) : Parcelable

// Wrapper untuk response GET /api/izin (keseluruhan)
@Parcelize
data class IzinListResponse(
    val success: Boolean,
    val message: String,
    val data: IzinPaginator
) : Parcelable

// Model untuk satu item Izin
@Parcelize
data class Izin(
    val id: Int,
    @SerializedName("siswa_id") val siswaId: Int,
    @SerializedName("kelas_id") val kelasId: Int,
    @SerializedName("tanggal_izin") val tanggalIzin: String,
    val alasan: String,
    @SerializedName("bukti_surat") val buktiSurat: String?,
    val status: String,
    val siswa: Siswa? // Menggunakan kembali model Siswa yang sudah ada
) : Parcelable

// Wrapper untuk response GET /api/izin/{id} (detail)
@Parcelize
data class IzinDetailResponse(
    val success: Boolean,
    val message: String,
    val data: Izin
) : Parcelable

// Wrapper untuk response POST /api/izin (store)
@Parcelize
data class IzinStoreResponse(
    val success: Boolean,
    val message: String,
    val data: Izin
) : Parcelable
