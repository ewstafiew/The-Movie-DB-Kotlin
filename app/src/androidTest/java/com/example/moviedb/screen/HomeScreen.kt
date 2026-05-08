package com.example.moviedb.screen

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.example.moviedb.compose.ui.HomeScreenTags

@OptIn(ExperimentalTestApi::class)
class HomeScreen(private val composeTest: ComposeTestRule) {

    fun waitUntilOpened(): HomeScreen = apply {
        composeTest.waitUntil(timeoutMillis = 10_000) {
            composeTest.onAllNodesWithTag(HomeScreenTags.ROOT, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun assertMovieVisible(movieId: String, title: String): HomeScreen = apply {
        composeTest.onNodeWithTag(HomeScreenTags.movieItem(movieId), useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithTag(HomeScreenTags.movieTitle(movieId), useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithText(title, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    fun openMovie(movieId: String): HomeScreen = apply {
        composeTest.onNodeWithTag(HomeScreenTags.movieItem(movieId), useUnmergedTree = true)
            .performClick()
    }

    fun assertOpenedAfterBackNavigation(): HomeScreen = apply {
        composeTest.onNodeWithTag(HomeScreenTags.GRID, useUnmergedTree = true)
            .assertIsDisplayed()
    }
}



