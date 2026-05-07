package com.example.moviedb.screen

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import com.example.moviedb.compose.ui.DetailScreenTags

@OptIn(ExperimentalTestApi::class)
class DetailScreen(private val composeTest: ComposeTestRule) {

    fun waitUntilLoaded(): DetailScreen = apply {
        composeTest.waitUntil(timeoutMillis = 10_000) {
            composeTest.onAllNodesWithTag(DetailScreenTags.ROOT, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun waitUntilEmptyStateVisible(): DetailScreen = apply {
        composeTest.waitUntil(timeoutMillis = 10_000) {
            composeTest.onAllNodesWithTag(DetailScreenTags.EMPTY_STATE, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun assertMovieDetails(title: String, releaseDate: String, overview: String, rating: String): DetailScreen = apply {
        composeTest.onNodeWithTag(DetailScreenTags.TITLE, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithText(title, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithTag(DetailScreenTags.RELEASE_DATE, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithText(releaseDate, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithTag(DetailScreenTags.OVERVIEW, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithText(overview, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithTag(DetailScreenTags.RATING, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithText(rating, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    fun assertNetworkErrorIsShown(): DetailScreen = apply {
        composeTest.onNodeWithText("Network error", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithText("No Internet Connection", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTest.onNodeWithTag(DetailScreenTags.REFRESH_ACTION, useUnmergedTree = true)
            .assertIsDisplayed()
    }

    fun dismissErrorDialog(): DetailScreen = apply {
        composeTest.onNodeWithText("OK", useUnmergedTree = true)
            .performClick()
    }

    fun assertErrorDialogDismissed(): DetailScreen = apply {
        composeTest.waitUntil(timeoutMillis = 5_000) {
            composeTest.onAllNodesWithText("OK", useUnmergedTree = true)
                .fetchSemanticsNodes().isEmpty()
        }
    }

    fun tapBack(): DetailScreen = apply {
        composeTest.onNodeWithTag(DetailScreenTags.BACK_BUTTON, useUnmergedTree = true)
            .performClick()
    }
}



