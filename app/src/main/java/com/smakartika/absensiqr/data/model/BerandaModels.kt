package com.smakartika.absensiqr.data.model

import com.google.gson.annotations.SerializedName

// Model untuk seluruh respons dari API /api/beranda
data class BerandaResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user")
    val user: UserData?,
    @SerializedName("role") val role: String,
    @SerializedName("statistik") val statistik: Statistik?,
    @SerializedName("jadwal") val jadwal: JadwalPaginator
)

// Model untuk objek paginasi dari Laravel
data class JadwalPaginator(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("data") val data: List<JadwalPelajaran>,
    @SerializedName("last_page") val lastPage: Int
)

// Model untuk satu item jadwal pelajaran
data class JadwalPelajaran(
    @SerializedName("id") val id: Int,
    @SerializedName("hari") val hari: String,
    @SerializedName("jam_mulai") val jamMulai: String,
    @SerializedName("jam_selesai") val jamSelesai: String,
    @SerializedName("mapel") val mapel: Mapel,
    @SerializedName("kelas") val kelas: Kelas,
    @SerializedName("guru") val guru: Guru
)
data class UserData(
    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val imageUrl: String? // Dibuat nullable jika gambar bisa kosong
)
// Model data pendukung yang berelasi
data class Mapel(@SerializedName("nama") val nama: String)
data class Kelas(@SerializedName("nama") val nama: String)
data class Guru(@SerializedName("user") val user: UserInfo)
data class UserInfo(@SerializedName("name") val name: String)
data class Statistik(@SerializedName("jumlah_user") val jumlahUser: Int)