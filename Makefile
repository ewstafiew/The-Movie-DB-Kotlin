# Android Studio plugin https://plugins.jetbrains.com/plugin/9333-makefile-language
# Build app command line https://developer.android.com/studio/build/building-cmdline
# Gradle https://docs.gradle.org/current/userguide/userguide.html

# common

.PHONY: tasks
tasks:
	./gradlew tasks

.PHONY: signing-report
signing-report:
	./gradlew signingReport

# single run

.PHONY: run-devDebug
run-devDebug:
	./gradlew installDevDebug
	adb shell am start -n com.example.moviedb.dev/com.example.moviedb.compose.main.ComposeActivity

.PHONY: run-devRelease
run-devRelease:
	./gradlew installDevRelease
	adb shell am start -n com.example.moviedb.dev/com.example.moviedb.compose.main.ComposeActivity

.PHONY: run-prdDebug
run-prdDebug:
	./gradlew installPrdDebug
	adb shell am start -n com.example.moviedb.dev/com.example.moviedb.compose.main.ComposeActivity

.PHONY: run-prdRelease
run-prdRelease:
	./gradlew installPrdRelease
	adb shell am start -n com.example.moviedb.dev/com.example.moviedb.compose.main.ComposeActivity

# single build

.PHONY: build-apk-devDebug
build-apk-devDebug:
	./gradlew :app:assembleDevDebug
	open app/build/outputs/apk/dev/debug

.PHONY: build-apk-devRelease
build-apk-devRelease:
	./gradlew :app:assembleDevRelease
	open app/build/outputs/apk/dev/release

.PHONY: build-bundle-devRelease
build-bundle-devRelease:
	./gradlew :app:bundleDevRelease
	open app/build/outputs/bundle/devRelease

.PHONY: build-apk-prdDebug
build-apk-prdDebug:
	./gradlew :app:assemblePrdDebug
	open app/build/outputs/apk/prd/debug

.PHONY: build-apk-prdRelease
build-apk-prdRelease:
	./gradlew :app:assemblePrdRelease
	open app/build/outputs/apk/prd/release

.PHONY: build-bundle-prdRelease
build-bundle-prdRelease:
	./gradlew :app:bundlePrdRelease
	open app/build/outputs/bundle/prdRelease

# multiple builds

.PHONY: build-apk-devDebug-prdRelease
build-apk-devDebug-prdRelease:
	$(MAKE) build-apk-devDebug
	$(MAKE) build-apk-prdRelease

.PHONY: build-bundle-devRelease-prdRelease
build-bundle-devRelease-prdRelease:
	$(MAKE) build-bundle-devRelease
	$(MAKE) build-bundle-prdRelease