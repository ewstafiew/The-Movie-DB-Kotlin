package com.example.moviedb.data.repository

import com.example.moviedb.data.local.dao.MovieDao
import com.example.moviedb.data.model.Movie
import com.example.moviedb.data.remote.api.ApiService
import com.example.moviedb.data.remote.response.GetMovieListResponse
import com.example.moviedb.data.repository.impl.UserRepositoryImpl
import io.qameta.allure.kotlin.junit4.AllureRunner
import io.qameta.allure.kotlin.Description
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AllureRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {


    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var movieDao: MovieDao

    private lateinit var repository: UserRepositoryImpl
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = UserRepositoryImpl(
            apiService = apiService,
            movieDao = movieDao,
            ioDispatcher = testDispatcher
        )
    }

    @Description("Проверяет, что получение списка фильмов " +
            "из API возвращает ожидаемые данные")
    @Test
    fun testGetMovieList_ReturnsMovies() = runTest {
        val mockResponse = GetMovieListResponse().apply {
            results = listOf(
                Movie(id = "1", title = "Test Movie 1", posterPath = "/path1.jpg"),
                Movie(id = "2", title = "Test Movie 2", posterPath = "/path2.jpg")
            )
        }
        val params = HashMap<String, String>()
        Mockito.`when`(apiService.getDiscoverMovie(params)).thenReturn(mockResponse)

        val result = repository.getMovieList(params)

        assertNotNull(result.results)
        assertEquals(2, result.results?.size)
        Mockito.verify(apiService).getDiscoverMovie(params)
    }

    @Description("Проверяет, что получение фильма по ID из API возвращает нужный фильм")
    @Test
    fun testGetMovieById_ReturnsMovie() = runTest {
        val mockMovie = Movie(id = "550", title = "Fight Club")
        Mockito.`when`(apiService.getMovie(movieId = "550")).thenReturn(mockMovie)

        val result = repository.getMovieById("550")

        assertEquals("550", result.id)
        assertEquals("Fight Club", result.title)
        Mockito.verify(apiService).getMovie(movieId = "550")
    }

    @Description("Проверяет, что при вставке фильма вызывается метод insert у DAO")
    @Test
    fun testInsertLocal_CallsDao() = runTest {
        val movie = Movie(id = "1", title = "Test")

        repository.insertLocal(movie)

        Mockito.verify(movieDao).insert(movie)
    }

    @Description("Проверяет, что получение локального" +
            " фильма по ID возвращает ожидаемый объект")
    @Test
    fun testGetMovieLocal_ReturnsMovie() = runTest {
        val mockMovie = Movie(id = "1", title = "Test")
        Mockito.`when`(movieDao.getMovie("1")).thenReturn(mockMovie)

        val result = repository.getMovieLocal("1")

        assertNotNull(result)
        assertEquals("1", result?.id)
        assertEquals("Test", result?.title)
        Mockito.verify(movieDao).getMovie("1")
    }


    @Description("Проверяет, что удаление локального фильма вызывает deleteMovie у DAO")
    @Test
    fun testDeleteMovieLocal_CallsDao() = runTest {
        repository.deleteMovieLocal("1")

        Mockito.verify(movieDao).deleteMovie("1")
    }

    @Description("Проверяет, что обновление локального фильма вызывает update у DAO")
    @Test
    fun testUpdateLocal_CallsDao() = runTest {
        val movie = Movie(id = "1", title = "Updated")

        repository.updateLocal(movie)

        Mockito.verify(movieDao).update(movie)
    }


    @Description("Проверяет, что получение локального " +
            "списка фильмов возвращает ожидаемое количество")
    @Test
    fun testGetMovieListLocal_ReturnsMovies() = runTest {
        val mockMovies = listOf(
            Movie(id = "1", title = "Movie 1"),
            Movie(id = "2", title = "Movie 2")
        )
        Mockito.`when`(movieDao.getMovieList()).thenReturn(mockMovies)

        val result = repository.getMovieListLocal()

        assertNotNull(result)
        assertEquals(2, result?.size)
        assertTrue(result?.isNotEmpty() == true)
        Mockito.verify(movieDao).getMovieList()
    }

    @Description("Проверяет, что удаление всех локальных фильмов вызывает deleteAll у DAO")
    @Test
    fun testDeleteAllLocal_CallsDao() = runTest {
        repository.deleteAllLocal()

        Mockito.verify(movieDao).deleteAll()
    }

    @Description("Проверяет, что получение избранных фильмов возвращает ожидаемые записи")
    @Test
    fun testGetFavoriteLocal_ReturnsFavorites() = runTest {
        val limit = 10
        val offset = 0
        val mockFavorites = listOf(
            Movie(id = "1", title = "Favorite 1", isFavorite = true)
        )
        Mockito.`when`(movieDao.getFavorite(limit, offset)).thenReturn(mockFavorites)

        val result = repository.getFavoriteLocal(limit, offset)

        assertNotNull(result)
        assertEquals(1, result?.size)
        assertTrue(result?.firstOrNull()?.isFavorite == true)
        Mockito.verify(movieDao).getFavorite(10, 0)
    }

    @Description("Проверяет, что при вставке нескольких фильмов " +
        "вызывается метод insert(List<Movie>) у DAO")
    @Test
    fun testInsertLocalMultiple_CallsDao() = runTest {
        val movies = listOf(
            Movie(id = "1", title = "Movie 1"),
            Movie(id = "2", title = "Movie 2")
        )

        repository.insertLocal(movies)

        Mockito.verify(movieDao).insert(movies)
    }
}

