package com.smakartika.absensiqr.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// --- DATA MODELS UNTUK API PROFILE ---

// Wrapper utama untuk response GET /api/profile
@Parcelize
data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: User
) : Parcelable

// Model User Utama
@Parcelize
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val image: String?,
    val siswa: Siswa?, // Nullable, karena bisa jadi Guru
) : Parcelable

@Parcelize
data class Siswa(
    val id: Int,
    val nis: String?,
    val nisn: String?,
    @SerializedName("jenis_kelamin")
    val jenisKelamin: String?,
    @SerializedName("no_telepon")
    val noTelepon: String?,
    val kelas: Kelas?,
    val ortu: Ortu?
) : Parcelable

@Parcelize
data class Guru(
    val id: Int,
    val nip: String?,
    @SerializedName("jenis_kelamin")
    val jenisKelamin: String?,
    @SerializedName("no_telepon")
    val noTelepon: String?,
    // 'user' ditambahkan dan dibuat nullable agar bisa menangani semua response API
    val user: UserInfo?
) : Parcelable
// Model UserInfo untuk data guru yang di-nest
@Parcelize
data class UserInfo(
    val name: String
) : Parcelable

@Parcelize
data class Kelas(
    val id: Int,
    val nama: String
) : Parcelable

@Parcelize
data class Ortu(
    val id: Int,
    val nama: String?,
    val alamat: String?,
    val pekerjaan: String?,
    @SerializedName("no_telepon")
    val noTelepon: String?,
    val hubungan: String?
) : Parcelable

// Wrapper untuk response POST /api/profile/update
@Parcelize
data class UpdateProfileResponse(
    val success: Boolean,
    val message: String,
    val data: User
) : Parcelable

// Data class untuk response dari endpoint GET /api/profile/edit
data class EditProfileDataResponse(
    val success: Boolean,
    val kelas: List<Kelas>
)

// Model untuk request ganti password
data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("new_password_confirmation")
    val newPasswordConfirmation: String
)

// Model untuk response ganti password
data class ChangePasswordResponse(
    val message: String
)