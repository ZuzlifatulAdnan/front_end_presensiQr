package com.smakartika.absensiqr.data.model

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// --- Model untuk GET /api/absen/scan-form ---

// Wrapper utama
data class ScanFormDataResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ScanFormData
)

data class ScanFormData(
    @SerializedName("kelas") val kelas: KelasDetail
)

// Model detail kelas dengan lokasi
@Parcelize
data class KelasDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    // PERBAIKAN: Tipe data diubah dari Int? menjadi String?
    @SerializedName("radius") val radius: String?
) : Parcelable

// Model untuk request body
data class AbsenScanRequest(
    @SerializedName("token_qr") val tokenQr: String,
    @SerializedName("lat") val latitude: Double,
    @SerializedName("long") val longitude: Double
)

// Model untuk response
data class AbsenScanResponse(
    @SerializedName("message") val message: String
)
