plugins {
    id("org.jetbrains.kotlin.jvm")
}

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
}

tasks.withType<Test>().configureEach {
    useJUnit()
    systemProperty("file.encoding", "UTF-8")
    systemProperty("allure.results.directory", "${rootProject.projectDir}/reports/allure-results/task-3-appium")

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
    useJUnit()
    filter {
        includeTestsMatching("com.example.moviedb.appium.MovieFlowAppiumBlackBoxTest")
    }
}

