package com.example.moviedb.appium.pages

import com.example.moviedb.appium.core.AppiumConfig
import io.appium.java_client.android.AndroidDriver

class DetailPage(
    driver: AndroidDriver,
    config: AppiumConfig,
) : BasePage(driver, config) {

    private companion object {
        const val DETAIL_SCREEN = "detail_screen"
        const val DETAIL_BACK_BUTTON = "detail_back_button"
        const val ERROR_DIALOG = "error_dialog"
    }

    fun waitOpened() {
        waitVisibleAny(
            timeoutSec = 25,
            byAccessibilityId(DETAIL_SCREEN),
            byUiSelectorDescription(DETAIL_SCREEN),
            byAccessibilityId(ERROR_DIALOG),
            byUiSelectorDescription(ERROR_DIALOG),
        )
    }

    fun waitBackButtonVisible() {
        waitVisibleAny(
            timeoutSec = 25,
            byAccessibilityId(DETAIL_BACK_BUTTON),
            byUiSelectorDescription(DETAIL_BACK_BUTTON),
        )
    }

    fun tapBack() {
        runCatching {
            val backButton = waitVisibleAny(
                timeoutSec = 5,
                byAccessibilityId(DETAIL_BACK_BUTTON),
                byUiSelectorDescription(DETAIL_BACK_BUTTON),
            )
            tapElementCenter(backButton)
        }.getOrElse {
            driver.navigate().back()
        }
    }
}

