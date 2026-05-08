package com.example.moviedb.appium.core

import io.qameta.allure.junit4.AllureJunit4
import org.junit.runner.JUnitCore
import kotlin.system.exitProcess

/**
 * Запускает JUnit4 тест-класс с подключенным Allure RunListener,
 * чтобы гарантированно формировались allure-results.
 */
fun main() {
    val testClass = Class.forName("com.example.moviedb.appium.MovieFlowAppiumBlackBoxTest")

    val junit = JUnitCore()
    junit.addListener(AllureJunit4())

    val result = junit.run(testClass)
    if (!result.wasSuccessful()) {
        exitProcess(1)
    }
}

