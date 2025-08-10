package com.smakartika.absensiqr.utils

// Sealed class untuk merepresentasikan state dari sebuah operasi (misal: panggilan API)
sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}