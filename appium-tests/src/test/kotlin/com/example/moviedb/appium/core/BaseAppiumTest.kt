package com.example.moviedb.appium.core

import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import org.junit.After
import org.junit.Assume
import org.junit.Before
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

abstract class BaseAppiumTest {

    protected val config: AppiumConfig = AppiumConfig.fromSystemProperties()
    protected lateinit var driver: AndroidDriver

    @Before
    fun setUpDriver() {
        Assume.assumeTrue(
            "Appium server недоступен по ${config.serverUrl}. Тест пропущен.",
            DriverFactory.isServerReachable(config.serverUrl)
        )
        driver = DriverFactory.create(config)
    }

    @After
    fun tearDownDriver() {
        if (::driver.isInitialized) {
            driver.quit()
        }
    }

    protected fun byId(id: String): By = AppiumBy.id("${config.appPackage}:id/$id")

    protected fun byComposeTag(tag: String): By = AppiumBy.id("${config.appPackage}:id/$tag")

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
}

