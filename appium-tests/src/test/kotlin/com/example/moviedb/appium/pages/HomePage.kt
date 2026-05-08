package com.example.moviedb.appium.pages

import com.example.moviedb.appium.core.AppiumConfig
import io.appium.java_client.android.AndroidDriver

class HomePage(
    driver: AndroidDriver,
    config: AppiumConfig,
) : BasePage(driver, config) {

    private companion object {
        const val HOME_SCREEN = "home_screen"
        const val HOME_MOVIES_GRID = "home_movies_grid"
        const val HOME_FIRST_MOVIE_CARD = "home_first_movie_card"
    }

    fun waitOpened() {
        waitVisibleAny(
            timeoutSec = 25,
            byAccessibilityId(HOME_SCREEN),
            byUiSelectorDescription(HOME_SCREEN),
        )
    }

    fun waitMoviesGridVisible() {
        waitVisibleAny(
            timeoutSec = 25,
            byAccessibilityId(HOME_MOVIES_GRID),
            byUiSelectorDescription(HOME_MOVIES_GRID),
        )
    }

    fun openFirstMovieCard() {
        val clickableMovieCard = waitVisibleAny(
            timeoutSec = 35,
            byAccessibilityId(HOME_FIRST_MOVIE_CARD),
            byUiSelectorDescription(HOME_FIRST_MOVIE_CARD),
        )
        runCatching {
            clickableMovieCard.click()
        }.getOrElse {
            tapElementCenter(clickableMovieCard)
        }
    }
}

