buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.49")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Libs.kotlinVersion}")
//        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Libs.navigationVersion}")
//        classpath("com.google.dagger:hilt-android-gradle-plugin:${Libs.daggerHiltVersion}")
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}

plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}
