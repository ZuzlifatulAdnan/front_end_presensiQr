package com.smakartika.absensiqr.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean, // <-- Laravel Anda tidak mengirim ini
    val message: String,  // <-- Laravel Anda tidak mengirim ini
    val token: String?,   // <-- Nama kuncinya 'token', bukan 'jwt-token'
    val user: User?
)