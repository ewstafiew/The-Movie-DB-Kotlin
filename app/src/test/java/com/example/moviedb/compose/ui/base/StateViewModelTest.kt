package com.example.moviedb.compose.ui.base

import com.example.moviedb.data.remote.BaseException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class StateViewModelTest {

    private lateinit var viewModel: StateViewModel

    @Before
    fun setUp() {
        viewModel = StateViewModel()
    }

    @Test
    fun showLoading_setsLoadingStateToTrue() = runBlocking {
        viewModel.showLoading()
        assertTrue(viewModel.loading.first())
    }

    @Test
    fun hideLoading_setsLoadingStateToFalse() = runBlocking {
        viewModel.showLoading()
        viewModel.hideLoading()
        assertFalse(viewModel.loading.first())
    }

    @Test
    fun showRefreshing_setsRefreshingStateToTrue() = runBlocking {
        viewModel.showRefreshing()
        assertTrue(viewModel.refreshing.first())
    }

    @Test
    fun hideRefreshing_setsRefreshingStateToFalse() = runBlocking {
        viewModel.showRefreshing()
        viewModel.hideRefreshing()
        assertFalse(viewModel.refreshing.first())
    }

    @Test
    fun onError_setsNetworkErrorEventForUnknownHostException() = runBlocking {
        viewModel.onError(UnknownHostException())
        assertEquals(ErrorEvent.Network, viewModel.errorEvent.first())
    }

    @Test
    fun onError_setsNetworkErrorEventForConnectException() = runBlocking {
        viewModel.onError(ConnectException())
        assertEquals(ErrorEvent.Network, viewModel.errorEvent.first())
    }

    @Test
    fun onError_setsTimeoutErrorEventForSocketTimeoutException() = runBlocking {
        viewModel.onError(SocketTimeoutException())
        assertEquals(ErrorEvent.Timeout, viewModel.errorEvent.first())
    }

    @Test
    fun onError_setsUnauthorizedErrorEventForHttpUnauthorized() = runBlocking {
        val baseException = HttpException(
            retrofit2.Response.error<Any>(
                HttpURLConnection.HTTP_UNAUTHORIZED,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )
        viewModel.onError(baseException)
        assertEquals(ErrorEvent.Unauthorized, viewModel.errorEvent.first())
    }

    @Test
    fun onError_setsUnknownErrorEventForOtherExceptions() = runBlocking {
        val baseException = BaseException.toNetworkError(Throwable("Unknown error"))
        viewModel.onError(baseException)
        assertTrue(viewModel.errorEvent.first() is ErrorEvent.Unknown)
    }

    @Test
    fun hideError_resetsErrorEventToNull() = runBlocking {
        viewModel.onError(UnknownHostException())
        viewModel.hideError()
        assertNull(viewModel.errorEvent.first())
    }
}