package com.example.moviedb.screen

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.moviedb.R

object SplashScreen {

    fun assertOpened() {
        onView(withId(R.id.splash_root)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_splash_logo)).check(matches(isDisplayed()))
    }
}

