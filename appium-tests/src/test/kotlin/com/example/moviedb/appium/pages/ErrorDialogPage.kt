package com.example.moviedb.appium.pages

import com.example.moviedb.appium.core.AppiumConfig
import io.appium.java_client.android.AndroidDriver

class ErrorDialogPage(
    driver: AndroidDriver,
    config: AppiumConfig,
) : BasePage(driver, config) {

    private companion object {
        const val ERROR_DIALOG = "error_dialog"
        const val ERROR_DIALOG_OK_BUTTON = "error_dialog_ok_button"
    }

    fun waitNetworkErrorVisible() {
        waitVisibleAny(
            timeoutSec = 20,
            byAccessibilityId(ERROR_DIALOG),
            byUiSelectorDescription(ERROR_DIALOG),
        )
    }

    fun closeIfVisible() {
        runCatching {
            waitVisibleAny(
                timeoutSec = 3,
                byAccessibilityId(ERROR_DIALOG),
                byUiSelectorDescription(ERROR_DIALOG),
            )
            closeByOk()
            assertClosed()
        }
    }

    fun closeByOk() {
        val okButton = waitVisibleAny(
            timeoutSec = 20,
            byAccessibilityId(ERROR_DIALOG_OK_BUTTON),
            byUiSelectorDescription(ERROR_DIALOG_OK_BUTTON),
        )
        tapElementCenter(okButton)
    }

    fun assertClosed() {
        waitInvisible(byAccessibilityId(ERROR_DIALOG), timeoutSec = 10)
    }
}

