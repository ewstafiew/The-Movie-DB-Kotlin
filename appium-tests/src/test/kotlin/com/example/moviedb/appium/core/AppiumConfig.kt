package com.example.moviedb.appium.core

data class AppiumConfig(
    val serverUrl: String,
    val deviceName: String,
    val platformVersion: String,
    val udid: String,
    val appPackage: String,
    val appActivity: String,
    val automationName: String,
    val adbPath: String,
) {
    companion object {
        fun fromSystemProperties(): AppiumConfig {
            return AppiumConfig(
                serverUrl = System.getProperty("appium.server.url", "http://127.0.0.1:4723"),
                deviceName = System.getProperty("appium.device.name", "Android Emulator"),
                platformVersion = System.getProperty("appium.platform.version", ""),
                udid = System.getProperty("appium.udid", ""),
                appPackage = System.getProperty("appium.app.package", "com.example.moviedb"),
                appActivity = System.getProperty("appium.app.activity", "com.example.moviedb.ui.screen.main.MainActivity"),
                automationName = System.getProperty("appium.automation.name", "UiAutomator2"),
                adbPath = System.getProperty("appium.adb.path", "adb"),
            )
        }
    }
}

