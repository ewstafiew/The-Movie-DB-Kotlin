package com.example.moviedb

import android.app.Application
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        if (isDevMode()) {
            // init timber
            Timber.plant(Timber.DebugTree())
        }
    }
}

@Suppress("KotlinConstantConditions")
fun isDevMode() = BuildConfig.BUILD_TYPE != "release"
