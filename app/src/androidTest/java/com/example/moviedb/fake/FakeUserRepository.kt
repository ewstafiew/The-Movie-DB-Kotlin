package com.example.moviedb.fake

import com.example.moviedb.data.model.Movie
import com.example.moviedb.data.remote.api.ApiParams
import com.example.moviedb.data.remote.response.GetCastAndCrewResponse
import com.example.moviedb.data.remote.response.GetMovieListResponse
import com.example.moviedb.data.remote.response.GetTvListResponse
import com.example.moviedb.data.repository.UserRepository
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeUserRepository @Inject constructor() : UserRepository {

    private val defaultMovies = listOf(
        Movie(
            id = "movie_1",
            title = "Inception",
            overview = "A thief who steals corporate secrets through dream-sharing technology.",
            releaseDate = "2010-07-16",
            voteAverage = 8.8,
            posterPath = "/inception.jpg",
            backdropPath = "/inception_backdrop.jpg"
        ),
        Movie(
            id = "movie_2",
            title = "Interstellar",
            overview = "A team travels through a wormhole in space in an attempt to ensure humanity's survival.",
            releaseDate = "2014-11-07",
            voteAverage = 8.6,
            posterPath = "/interstellar.jpg",
            backdropPath = "/interstellar_backdrop.jpg"
        )
    )

    var movies: List<Movie> = defaultMovies
    var movieDetails: MutableMap<String, Movie> = defaultMovies.associateBy { it.id }.toMutableMap()
    var movieListError: Exception? = null
    var movieDetailError: Exception? = null

    fun reset() {
        movies = defaultMovies
        movieDetails = defaultMovies.associateBy { it.id }.toMutableMap()
        movieListError = null
        movieDetailError = null
    }

    fun makeMovieDetailFailWithNetworkError() {
        movieDetailError = UnknownHostException("No internet in fake repository")
    }

    override suspend fun getMovieList(hashMap: HashMap<String, String>): GetMovieListResponse {
        movieListError?.let { throw it }
        val page = hashMap[ApiParams.PAGE]?.toIntOrNull() ?: 1
        return GetMovieListResponse().apply {
            results = if (page == 1) movies else emptyList()
        }
    }

    override suspend fun getMovieById(movieId: String): Movie {
        movieDetailError?.let { throw it }
        return movieDetails[movieId] ?: throw NoSuchElementException("Movie with id=$movieId was not found")
    }

    override suspend fun getCastAndCrew(movieId: String): GetCastAndCrewResponse = GetCastAndCrewResponse()

    override suspend fun getTvList3(hashMap: HashMap<String, String>): GetTvListResponse = GetTvListResponse()

    override suspend fun insertDB(list: List<Movie>) = Unit

    override suspend fun updateDB(movie: Movie) = TODO("не используется в тесте")

    override suspend fun getMovieListLocal(): List<Movie> = movies

    override suspend fun getMovieLocal(id: String): Movie? = movieDetails[id]

    override suspend fun insertLocal(movie: Movie) {
        movieDetails[movie.id] = movie
        movies = movies + movie
    }

    override suspend fun insertLocal(list: List<Movie>) {
        movieDetails.putAll(list.associateBy { it.id })
        movies = movies + list
    }

    override suspend fun updateLocal(movie: Movie) {
        movieDetails[movie.id] = movie
        movies = movies.map { current -> if (current.id == movie.id) movie else current }
    }

    override suspend fun deleteMovieLocal(movie: Movie) {
        deleteMovieLocal(movie.id)
    }

    override suspend fun deleteMovieLocal(id: String) {
        movieDetails.remove(id)
        movies = movies.filterNot { it.id == id }
    }

    override suspend fun deleteAllLocal() {
        movieDetails.clear()
        movies = emptyList()
    }

    override suspend fun getMoviePageLocal(pageSize: Int, pageIndex: Int): List<Movie> = movies

    override suspend fun getFavoriteLocal(pageSize: Int, pageIndex: Int): List<Movie> = movies.filter { it.isFavorite == true }
}


