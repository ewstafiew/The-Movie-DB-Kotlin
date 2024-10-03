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
            /*
            init timber log
            use tag:mdb to filter custom log in android studio/logcat window
             */
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return "mdb ${super.createStackElementTag(element)}"
                }
            })
        }
    }
}

@Suppress("KotlinConstantConditions")
fun isDevMode() = BuildConfig.BUILD_TYPE != "release"
