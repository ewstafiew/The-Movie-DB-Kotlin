package com.example.moviedb.appium.core

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import java.net.HttpURLConnection
import java.net.URL

object DriverFactory {

    fun isServerReachable(serverUrl: String): Boolean {
        return runCatching {
            val statusUrl = URL(serverUrl.trimEnd('/') + "/status")
            val connection = statusUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 2_000
            connection.readTimeout = 2_000
            connection.connect()
            connection.responseCode in 200..299
        }.getOrDefault(false)
    }

    fun create(config: AppiumConfig): AndroidDriver {
        val options = UiAutomator2Options()
            .setPlatformName("Android")
            .setAutomationName(config.automationName)
            .setDeviceName(config.deviceName)
            .setAppPackage(config.appPackage)
            .setAppActivity(config.appActivity)
            .setNoReset(false)
            .amend("fullReset", false)
            .amend("autoGrantPermissions", true)
            .amend("autoLaunch", true)
            .amend("appWaitForLaunch", true)
            .amend("newCommandTimeout", 120)

        if (config.udid.isNotBlank()) {
            options.setUdid(config.udid)
        }
        if (config.platformVersion.isNotBlank()) {
            options.setPlatformVersion(config.platformVersion)
        }

        return AndroidDriver(URL(config.serverUrl), options)
    }
}

