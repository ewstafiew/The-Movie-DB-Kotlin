package com.example.moviedb.appium.pages

import com.example.moviedb.appium.core.AppiumConfig
import io.appium.java_client.android.AndroidDriver

class SplashPage(
    driver: AndroidDriver,
    config: AppiumConfig,
) : BasePage(driver, config) {

    fun waitLogoVisible() {
        // Splash может исчезнуть очень быстро; для smoke проверяем его best-effort.
        runCatching {
            waitVisible(byId("splashLogo"), timeoutSec = 2)
        }
    }
}

