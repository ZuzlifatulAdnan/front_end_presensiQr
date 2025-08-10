package com.smakartika.absensiqr.data.model

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Wrapper utama untuk response paginasi dari Laravel
//@Parcelize
//data class JadwalPaginator(
//    @SerializedName("current_page") val currentPage: Int,
//    @SerializedName("data") val data: List<JadwalPelajaran>,
//    @SerializedName("last_page") val lastPage: Int,
//    @SerializedName("total") val total: Int
//) : Parcelable

//@Parcelize
//data class JadwalPelajaran(
//    @SerializedName("id") val id: Int,
//    @SerializedName("hari") val hari: String,
//    @SerializedName("jam_mulai") val jamMulai: String,
//    @SerializedName("jam_selesai") val jamSelesai: String,
//    @SerializedName("mapel") val mapel: Mapel,
//    // Menggunakan kembali model Kelas dan Guru yang sudah ada
//    @SerializedName("kelas") val kelas: Kelas,
//    @SerializedName("guru") val guru: Guru,
//    @SerializedName("tanggal_pertemuan") val tanggalPertemuan: String?
//) : Parcelable

// Model data pendukung yang mungkin belum ada
//@Parcelize
//data class Mapel(
//    @SerializedName("id") val id: Int,
//    @SerializedName("nama") val nama: String
//) : Parcelable