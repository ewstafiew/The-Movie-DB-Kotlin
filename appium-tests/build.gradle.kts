import org.gradle.internal.os.OperatingSystem

plugins {
    id("org.jetbrains.kotlin.jvm")
}

val allureVersion = "2.29.0"
val allureResultsDir = rootProject.layout.projectDirectory.dir("reports/allure-results/task-3-appium")
val allureReportDir = rootProject.layout.projectDirectory.dir("reports/allure-report/task-3-appium")

val allureCli by configurations.creating

repositories {
    mavenCentral()
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")

    testImplementation("io.appium:java-client:9.3.0")
    testImplementation("org.seleniumhq.selenium:selenium-support:4.24.0")

    testImplementation("io.qameta.allure:allure-junit4:2.29.0")
    testImplementation("io.qameta.allure:allure-java-commons:2.29.0")

    allureCli("io.qameta.allure:allure-commandline:$allureVersion@zip")
}

tasks.withType<Test>().configureEach {
    useJUnit()
    systemProperty("file.encoding", "UTF-8")
    systemProperty("allure.results.directory", allureResultsDir.asFile.absolutePath)

    // Параметры можно переопределить через -D...
    systemProperty("appium.server.url", System.getProperty("appium.server.url", "http://127.0.0.1:4723"))
    systemProperty("appium.device.name", System.getProperty("appium.device.name", "Android Emulator"))
    systemProperty("appium.platform.version", System.getProperty("appium.platform.version", ""))
    systemProperty("appium.udid", System.getProperty("appium.udid", ""))
    systemProperty("appium.app.package", System.getProperty("appium.app.package", "com.example.moviedb"))
    systemProperty("appium.app.activity", System.getProperty("appium.app.activity", "com.example.moviedb.ui.screen.main.MainActivity"))
    systemProperty("appium.automation.name", System.getProperty("appium.automation.name", "UiAutomator2"))
    systemProperty("appium.adb.path", System.getProperty("appium.adb.path", "adb"))
}

tasks.register<Test>("testMovieFlowAppium") {
    group = "verification"
    description = "Запускает только Appium black-box тесты из MovieFlowAppiumBlackBoxTest"
    doFirst {
        delete(allureResultsDir)
        allureResultsDir.asFile.mkdirs()
    }
    filter {
        includeTestsMatching("com.example.moviedb.appium.MovieFlowAppiumBlackBoxTest")
    }
}

tasks.register<JavaExec>("runMovieFlowAppiumForAllure") {
    group = "verification"
    description = "Запускает MovieFlowAppiumBlackBoxTest через JUnitCore с Allure listener"
    dependsOn("testClasses")
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.example.moviedb.appium.core.AllureJunit4LauncherKt")

    systemProperty("file.encoding", "UTF-8")
    systemProperty("allure.results.directory", allureResultsDir.asFile.absolutePath)
    systemProperty("appium.server.url", System.getProperty("appium.server.url", "http://127.0.0.1:4723"))
    systemProperty("appium.device.name", System.getProperty("appium.device.name", "Android Emulator"))
    systemProperty("appium.platform.version", System.getProperty("appium.platform.version", ""))
    systemProperty("appium.udid", System.getProperty("appium.udid", ""))
    systemProperty("appium.app.package", System.getProperty("appium.app.package", "com.example.moviedb"))
    systemProperty("appium.app.activity", System.getProperty("appium.app.activity", "com.example.moviedb.ui.screen.main.MainActivity"))
    systemProperty("appium.automation.name", System.getProperty("appium.automation.name", "UiAutomator2"))
    systemProperty("appium.adb.path", System.getProperty("appium.adb.path", "adb"))

    doFirst {
        delete(allureResultsDir)
        allureResultsDir.asFile.mkdirs()
    }
}

val unpackAllureCli by tasks.registering(Sync::class) {
    from({ zipTree(allureCli.singleFile) })
    into(layout.buildDirectory.dir("allure-cli"))
}

tasks.register<Exec>("generateMovieFlowAllureReport") {
    group = "verification"
    description = "Генерирует Allure-отчет для Appium тестов в reports/allure-report/task-3-appium"
    dependsOn("runMovieFlowAppiumForAllure", unpackAllureCli)

    val os = OperatingSystem.current()
    val allureExecutable = if (os.isWindows) {
        layout.buildDirectory.file("allure-cli/allure-$allureVersion/bin/allure.bat").get().asFile
    } else {
        layout.buildDirectory.file("allure-cli/allure-$allureVersion/bin/allure").get().asFile
    }

    doFirst {
        val hasResults = allureResultsDir.asFile.exists() &&
                allureResultsDir.asFile.walkTopDown().any { it.isFile && it.extension == "json" }
        require(hasResults) {
            "Allure results не найдены в ${allureResultsDir.asFile.absolutePath}. " +
                    "Сначала запустите тесты и убедитесь, что Appium server доступен."
        }

        delete(allureReportDir)
        allureReportDir.asFile.mkdirs()
        if (!os.isWindows) {
            allureExecutable.setExecutable(true)
        }
    }

    commandLine(
        allureExecutable.absolutePath,
        "generate",
        allureResultsDir.asFile.absolutePath,
        "-o",
        allureReportDir.asFile.absolutePath,
        "--clean"
    )
}

tasks.register("runMovieFlowWithAllure") {
    group = "verification"
    description = "Запускает MovieFlowAppiumBlackBoxTest и собирает Allure-отчет"
    dependsOn("generateMovieFlowAllureReport")
}

