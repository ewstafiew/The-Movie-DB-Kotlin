package com.example.moviedb.appium.core

object NetworkController {

    fun disableNetwork(config: AppiumConfig) {
        runAdb(config, listOf("shell", "svc", "wifi", "disable"))
        runAdb(config, listOf("shell", "svc", "data", "disable"))
    }

    fun enableNetwork(config: AppiumConfig) {
        runAdb(config, listOf("shell", "svc", "wifi", "enable"))
        runAdb(config, listOf("shell", "svc", "data", "enable"))
    }

    private fun runAdb(config: AppiumConfig, args: List<String>) {
        val serialArgs = if (config.udid.isBlank()) emptyList() else listOf("-s", config.udid)
        val command = listOf(config.adbPath) + serialArgs + args

        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        process.inputStream.bufferedReader().use { it.readText() }
        process.waitFor()
    }
}

