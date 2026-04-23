package com.example.moviedb.compose.ui

object HomeScreenTags {
    const val ROOT = "home_screen_root"
    const val GRID = "home_movies_grid"

    fun movieItem(movieId: String) = "home_movie_item_$movieId"
    fun movieTitle(movieId: String) = "home_movie_title_$movieId"
}

object DetailScreenTags {
    const val ROOT = "detail_screen_root"
    const val BACK_BUTTON = "detail_back_button"
    const val TITLE = "detail_title"
    const val RELEASE_DATE = "detail_release_date"
    const val OVERVIEW = "detail_overview"
    const val RATING = "detail_rating"
    const val EMPTY_STATE = "detail_empty_state"
    const val REFRESH_ACTION = "detail_refresh_action"
}

