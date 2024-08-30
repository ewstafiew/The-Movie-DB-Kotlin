package com.example.moviedb.compose.ui.base

import com.example.moviedb.data.remote.BaseException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ErrorEventTest {

    @Test
    fun networkErrorEvent_hasCorrectType() {
        val event = ErrorEvent.Network
        assertEquals(ErrorType.NETWORK, event.type)
    }

    @Test
    fun timeoutErrorEvent_hasCorrectType() {
        val event = ErrorEvent.Timeout
        assertEquals(ErrorType.TIMEOUT, event.type)
    }

    @Test
    fun unauthorizedErrorEvent_hasCorrectType() {
        val event = ErrorEvent.Unauthorized
        assertEquals(ErrorType.HTTP_UNAUTHORIZED, event.type)
    }

    @Test
    fun forceUpdateErrorEvent_hasCorrectType() {
        val event = ErrorEvent.ForceUpdate
        assertEquals(ErrorType.FORCE_UPDATE, event.type)
    }

    @Test
    fun unknownErrorEvent_hasCorrectTypeAndBaseException() {
        val baseException = BaseException.toUnexpectedError(Throwable("Unknown error"))
        val event = ErrorEvent.Unknown(baseException)
        assertEquals(ErrorType.UNKNOWN, event.type)
        assertEquals(baseException, event.baseException)
    }

    @Test
    fun unknownErrorEvent_baseExceptionIsNotNull() {
        val baseException = BaseException.toUnexpectedError(Throwable("Unknown error"))
        val event = ErrorEvent.Unknown(baseException)
        assertNotNull(event.baseException)
    }

    @Test
    fun errorEvent_baseExceptionIsNullForNetwork() {
        val event = ErrorEvent.Network
        assertNull(event.baseException)
    }

    @Test
    fun errorEvent_baseExceptionIsNullForTimeout() {
        val event = ErrorEvent.Timeout
        assertNull(event.baseException)
    }

    @Test
    fun errorEvent_baseExceptionIsNullForUnauthorized() {
        val event = ErrorEvent.Unauthorized
        assertNull(event.baseException)
    }

    @Test
    fun errorEvent_baseExceptionIsNullForForceUpdate() {
        val event = ErrorEvent.ForceUpdate
        assertNull(event.baseException)
    }
}