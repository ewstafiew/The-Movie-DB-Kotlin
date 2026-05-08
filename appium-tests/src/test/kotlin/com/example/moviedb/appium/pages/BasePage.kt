package com.example.moviedb.appium.pages

import com.example.moviedb.appium.core.AppiumConfig
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

open class BasePage(
    protected val driver: AndroidDriver,
    protected val config: AppiumConfig,
) {
    protected fun byId(id: String): By = AppiumBy.id("${config.appPackage}:id/$id")

    protected fun byComposeTag(tag: String): By = AppiumBy.id("${config.appPackage}:id/$tag")

    protected fun byAccessibilityId(id: String): By = AppiumBy.accessibilityId(id)

    protected fun byUiSelectorDescription(value: String): By =
        AppiumBy.androidUIAutomator("new UiSelector().description(\"$value\")")


    protected fun waitVisible(locator: By, timeoutSec: Long = 15): WebElement {
        val wait = WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
    }

    protected fun waitClickable(locator: By, timeoutSec: Long = 15): WebElement {
        val wait = WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
        return wait.until(ExpectedConditions.elementToBeClickable(locator))
    }

    protected fun waitInvisible(locator: By, timeoutSec: Long = 10): Boolean {
        val wait = WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator))
    }

    protected fun tapElementCenter(element: WebElement) {
        val rect = element.rect
        val x = rect.x + rect.width / 2
        val y = rect.y + rect.height / 2
        driver.executeScript("mobile: clickGesture", mapOf("x" to x, "y" to y))
    }

    protected fun waitVisibleAny(timeoutSec: Long = 20, vararg locators: By): WebElement {
        val timeoutMs = timeoutSec * 1000
        val start = System.currentTimeMillis()
        var lastError: Throwable? = null

        while (System.currentTimeMillis() - start < timeoutMs) {
            for (locator in locators) {
                try {
                    return waitVisible(locator, timeoutSec = 2)
                } catch (error: TimeoutException) {
                    lastError = error
                }
            }
        }
        throw TimeoutException("Не найден ни один из ожидаемых локаторов: ${locators.joinToString()}", lastError)
    }
}

