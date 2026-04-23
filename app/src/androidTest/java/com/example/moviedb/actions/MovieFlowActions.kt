package com.example.moviedb.actions

import com.example.moviedb.screen.DetailScreen
import com.example.moviedb.screen.HomeScreen
import com.example.moviedb.screen.SplashScreen

class MovieFlowActions(
    private val homeScreen: HomeScreen,
    private val detailScreen: DetailScreen,
) {

    /** Проверяет, что splash-экран
     * отображается при запуске приложения. */
    fun checkSplashScreenIsDisplayed() {
        SplashScreen.assertOpened()
    }

    /** Находит карточку фильма на главном экране
     *  и открывает экран деталей выбранного фильма. */
    fun openMovieFromHome(movieId: String, title: String) {
        homeScreen
            .waitUntilOpened()
            .assertMovieVisible(movieId = movieId, title = title)
            .openMovie(movieId = movieId)
    }

    /** Проверяет, что на экране деталей
     * отображаются основные данные фильма. */
    fun checkMovieDetails(
        title: String,
        releaseDate: String,
        overview: String,
        rating: String,
    ) {
        detailScreen
            .waitUntilLoaded()
            .assertMovieDetails(
                title = title,
                releaseDate = releaseDate,
                overview = overview,
                rating = rating,
            )
    }

    /** Проверяет, что на экране деталей
     * показано сообщение о сетевой ошибке. */
    fun checkNetworkErrorOnDetailScreen() {
        detailScreen
            .waitUntilEmptyStateVisible()
            .assertNetworkErrorIsShown()
    }

    /** Закрывает диалог с ошибкой на экране деталей. */
    fun closeErrorDialog() {
        detailScreen.dismissErrorDialog()
    }

    /** Возвращается с экрана деталей на главный
     * экран и проверяет, что переход выполнен. */
    fun returnToHomeFromDetailScreen() {
        detailScreen.tapBack()
        homeScreen.assertOpenedAfterBackNavigation()
    }
}


