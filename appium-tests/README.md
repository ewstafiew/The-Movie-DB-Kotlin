# Appium Black-box Tests

Модуль `appium-tests` содержит E2E black-box сценарии для `The-Movie-DB-Kotlin`.

Покрытые сценарии (`MovieFlowAppiumBlackBoxTest`):
1. `Splash -> Home` (основной сценарий)
2. Home: отображение сетки фильмов (основной сценарий)
3. `Home -> Detail -> Back` (основной сценарий)
4. Ошибочный сценарий: отключение сети -> `Network error` диалог на Detail

## Быстрый запуск

1. Установить приложение на устройство/эмулятор (`prdDebug`):

```bash
cd /Users/evstafevigoraleksandrovic/StudioProjects/The-Movie-DB-Kotlin
./gradlew :app:installPrdDebug
```

2. Запустить Appium server:

```bash
appium --port 4723
```

3. Запустить Appium-тесты:

```bash
cd /Users/evstafevigoraleksandrovic/StudioProjects/The-Movie-DB-Kotlin
./gradlew :appium-tests:test
```

## Запуск только одного класса

```bash
cd /Users/evstafevigoraleksandrovic/StudioProjects/The-Movie-DB-Kotlin
./gradlew :appium-tests:test --tests "com.example.moviedb.appium.MovieFlowAppiumBlackBoxTest"
```

## Параметры запуска (опционально)

```bash
./gradlew :appium-tests:test \
  -Dappium.server.url=http://127.0.0.1:4723 \
  -Dappium.device.name="Android Emulator" \
  -Dappium.udid=emulator-5554 \
  -Dappium.app.package=com.example.moviedb \
  -Dappium.app.activity=com.example.moviedb.ui.screen.main.MainActivity
```

По умолчанию тесты помечены как `skipped`, если Appium server недоступен.

