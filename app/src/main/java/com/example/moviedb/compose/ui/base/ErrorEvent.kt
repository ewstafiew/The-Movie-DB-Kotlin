package com.example.moviedb.compose.ui.base

import com.example.moviedb.data.remote.BaseException

@Suppress("unused")
sealed class ErrorEvent(
    val type: ErrorType,
    val baseException: BaseException? = null
) {
    data object Network : ErrorEvent(type = ErrorType.NETWORK)
    data object Timeout : ErrorEvent(type = ErrorType.TIMEOUT)
    data object Unauthorized : ErrorEvent(type = ErrorType.HTTP_UNAUTHORIZED)
    data object ForceUpdate : ErrorEvent(type = ErrorType.FORCE_UPDATE)
    class Unknown(baseException: BaseException) :
        ErrorEvent(type = ErrorType.UNKNOWN, baseException = baseException)
}

enum class ErrorType {
    NETWORK,
    TIMEOUT,
    HTTP_UNAUTHORIZED,
    FORCE_UPDATE,
    UNKNOWN
}
