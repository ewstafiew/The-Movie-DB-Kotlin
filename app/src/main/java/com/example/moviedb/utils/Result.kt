package com.example.moviedb.utils

@Suppress("unused")
sealed class Result<out R> {
    data object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}