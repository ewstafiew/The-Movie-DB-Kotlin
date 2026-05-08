import java.io.ByteArrayOutputStream

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.4")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Libs.kotlinVersion}")
//        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Libs.navigationVersion}")
//        classpath("com.google.dagger:hilt-android-gradle-plugin:${Libs.daggerHiltVersion}")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}

val reportsDir = rootProject.layout.projectDirectory.dir("reports")
val devResultsDir = reportsDir.dir("allure-results/devDebug")
val devReportDir = reportsDir.dir("allure-report/devDebug")
val deviceAllureResultsPath = "/storage/emulated/0/Documents/allure-results"

val clearAllureResultsOnDeviceDevDebug = tasks.register("clearAllureResultsOnDeviceDevDebug") {
    group = "verification"
    description = "Clear previous Allure results on connected device for a clean report"
    doLast {
        exec {
            commandLine("sh", "-c", "adb shell 'rm -rf ${deviceAllureResultsPath}/* && mkdir -p ${deviceAllureResultsPath}'")
        }
    }
}

project(":app").tasks.configureEach {
    if (name == "connectedDevDebugAndroidTest") {
        mustRunAfter(clearAllureResultsOnDeviceDevDebug)
    }
}

tasks.register("collectAllureResultsDevDebug") {
    group = "verification"
    description = "Pull Allure raw results from connected device to reports/allure-results/devDebug"
    dependsOn(clearAllureResultsOnDeviceDevDebug, ":app:connectedDevDebugAndroidTest")
    doLast {
        delete(devResultsDir.asFile)
        devResultsDir.asFile.mkdirs()

        exec {
            commandLine(
                "adb", "pull",
                "$deviceAllureResultsPath/.",
                devResultsDir.asFile.absolutePath
            )
            isIgnoreExitValue = true
        }

        val verifyOutput = ByteArrayOutputStream()
        exec {
            commandLine("sh", "-c", "ls -1 \"${devResultsDir.asFile.absolutePath}\" 2>/dev/null | wc -l")
            standardOutput = verifyOutput
            isIgnoreExitValue = true
        }
        val count = verifyOutput.toString().trim().toIntOrNull() ?: 0
        check(count > 0) {
            "Allure results are empty in ${devResultsDir.asFile.absolutePath}. Run tests and verify device path."
        }
    }
}

// ─── Single-class Allure helpers ────────────────────────────────────────────

/**
 * Runs instrumented tests for ONE class, pulls raw Allure results from the device
 * and generates an HTML report.
 *
 * Usage:
 *   ./gradlew runAllureForClass -PtestClass=com.example.moviedb.kaspresso.MovieFlowKaspressoTest
 *
 * The report is written to:
 *   reports/allure-report/devDebug/<SimpleClassName>/index.html
 */
val testClassParam: String = project.findProperty("testClass") as String? ?: ""

tasks.register("clearAllureResultsForClass") {
    group = "verification"
    description = "Clear Allure results on device before a single-class run"
    doLast {
        exec {
            commandLine("sh", "-c", "adb shell 'rm -rf ${deviceAllureResultsPath}/* && mkdir -p ${deviceAllureResultsPath}'")
        }
    }
}

tasks.register("runInstrumentedTestForClass") {
    group = "verification"
    description = "Run instrumented tests for a single class (pass -PtestClass=<FQN>)"
    dependsOn("clearAllureResultsForClass")
    mustRunAfter("clearAllureResultsForClass")
    doLast {
        require(testClassParam.isNotBlank()) {
            "Specify the class with: ./gradlew runAllureForClass -PtestClass=com.example.ClassName"
        }
        exec {
            commandLine(
                "sh", "-c",
                "./gradlew :app:connectedDevDebugAndroidTest " +
                    "-Pandroid.testInstrumentationRunnerArguments.class=$testClassParam"
            )
        }
    }
}

tasks.register("collectAllureResultsForClass") {
    group = "verification"
    description = "Pull Allure results from device after a single-class run"
    dependsOn("runInstrumentedTestForClass")
    doLast {
        val simpleName = testClassParam.substringAfterLast('.')
        val classResultsDir = reportsDir.dir("allure-results/devDebug/$simpleName").asFile
        delete(classResultsDir)
        classResultsDir.mkdirs()

        exec {
            commandLine(
                "adb", "pull",
                "$deviceAllureResultsPath/.",
                classResultsDir.absolutePath
            )
            isIgnoreExitValue = true
        }

        val count = classResultsDir.listFiles()?.size ?: 0
        check(count > 0) {
            "Allure results are empty for $testClassParam. Check that the class name is correct and the device is connected."
        }
    }
}

tasks.register("runAllureForClass") {
    group = "verification"
    description = "Run tests for ONE class and generate an Allure HTML report.\n" +
        "  Usage: ./gradlew runAllureForClass -PtestClass=com.example.ClassName"
    dependsOn("collectAllureResultsForClass")
    doLast {
        val simpleName = testClassParam.substringAfterLast('.')
        val classResultsDir = reportsDir.dir("allure-results/devDebug/$simpleName").asFile
        val classReportDir  = reportsDir.dir("allure-report/devDebug/$simpleName").asFile
        delete(classReportDir)
        classReportDir.mkdirs()

        // Если Allure-результаты с устройства пусты — используем JUnit XML с хоста
        val sourceDir = if ((classResultsDir.listFiles()?.size ?: 0) > 0) {
            classResultsDir.absolutePath
        } else {
            println("⚠️  Device Allure results empty, falling back to JUnit XML from host build output.")
            "${project(":app").layout.buildDirectory.asFile.get()}" +
                "/outputs/androidTest-results/connected/debug/flavors/dev"
        }

        exec {
            commandLine(
                "sh", "-c",
                "if command -v allure >/dev/null 2>&1; then " +
                    "allure generate \"$sourceDir\" " +
                    "-o \"${classReportDir.absolutePath}\" --clean; " +
                    "else echo 'Allure CLI not found. Install with: brew install allure'; exit 1; fi"
            )
        }
        println("✅ Allure report for $simpleName: ${classReportDir.absolutePath}/index.html")
    }
}

// ─── Full-suite Allure report ────────────────────────────────────────────────

tasks.register("generateAllureReportDevDebug") {
    group = "verification"
    description = "Generate Allure HTML report to reports/allure-report/devDebug (requires allure CLI)"
    dependsOn("collectAllureResultsDevDebug")
    doLast {
        delete(devReportDir.asFile)
        devReportDir.asFile.mkdirs()
        exec {
            commandLine(
                "sh", "-c",
                "if command -v allure >/dev/null 2>&1; then " +
                    "allure generate \"${devResultsDir.asFile.absolutePath}\" " +
                    "-o \"${devReportDir.asFile.absolutePath}\" --clean; " +
                    "else echo 'Allure CLI not found. Install with: brew install allure'; exit 1; fi"
            )
        }
    }
}

plugins {
    // https://github.com/google/ksp/releases
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    // https://developer.android.com/develop/ui/compose/compiler
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}
