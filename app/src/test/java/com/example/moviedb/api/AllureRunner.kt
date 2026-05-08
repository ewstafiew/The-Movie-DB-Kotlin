package com.example.moviedb.api

import io.qameta.allure.junit4.AllureJunit4
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner

/**
 * Custom JUnit4 runner that registers AllureJunit4 listener manually,
 * because allure-junit4 does not provide ServiceLoader registration.
 */
class AllureRunner(clazz: Class<*>) : BlockJUnit4ClassRunner(clazz) {
    override fun run(notifier: RunNotifier) {
        notifier.addListener(AllureJunit4())
        super.run(notifier)
    }
}

