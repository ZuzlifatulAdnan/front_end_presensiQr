package com.smakartika.absensiqr.data.model

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Wrapper utama untuk response
data class RekapAbsenResponse(
    @SerializedName("jadwal") val jadwal: JadwalPelajaran,
    @SerializedName("jumlah_pertemuan") val jumlahPertemuan: Int,
    @SerializedName("tanggal_absen") val tanggalAbsen: Map<String, String>,
    @SerializedName("rekap") val rekap: Map<String, List<Absen>>
)

// Model untuk satu data absen (ini tetap Parcelable karena mungkin digunakan di tempat lain)
@Parcelize
data class Absen(
    @SerializedName("id") val id: Int,
    @SerializedName("jadwal_id") val jadwalId: Int,
    @SerializedName("siswa_id") val siswaId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("pertemuan_ke") val pertemuanKe: Int,
    @SerializedName("tanggal_absen") val tanggalAbsen: String,
    @SerializedName("siswa") val siswa: Siswa
) : Parcelable