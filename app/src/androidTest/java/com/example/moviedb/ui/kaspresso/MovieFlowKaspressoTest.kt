package com.example.moviedb.ui.kaspresso

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.example.moviedb.actions.MovieFlowActions
import com.example.moviedb.compose.ComposeActivity
import com.example.moviedb.fake.FakeUserRepository
import com.example.moviedb.screen.DetailScreen
import com.example.moviedb.screen.HomeScreen
import com.example.moviedb.ui.screen.main.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@OptIn(ExperimentalTestApi::class)
class MovieFlowKaspressoTest : TestCase() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var fakeUserRepository: FakeUserRepository

    private val movieFlowActions by lazy {
        MovieFlowActions(
            homeScreen = HomeScreen(composeRule),
            detailScreen = DetailScreen(composeRule),
        )
    }

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeUserRepository.reset()
    }

    /** Проверяет, что при запуске приложения отображается splash-экран с логотипом. */
    @Test
    fun splashScreen_displaysLogoOnAppStart() {
        run {
            val scenario = launchActivity<MainActivity>()
            try {
                step("Проверка отображения splash-экрана при запуске приложения") {
                    movieFlowActions.checkSplashScreenIsDisplayed()
                }
            } finally {
                scenario.close()
            }
        }
    }

    /** Проверяет, что на главном экране отображается список фильмов
     *  и из него можно перейти в детали фильма. */
    @Test
    fun homeScreen_opensMovieDetailsFromMovieCard() {
        run {
            val scenario = launchActivity<ComposeActivity>()
            try {
                step("Ожидаем загрузку главного экрана и проверяем карточку фильма") {
                    movieFlowActions.openMovieFromHome(movieId = "movie_1", title = "Inception")
                }

                step("Проверяем экран деталей") {
                    movieFlowActions.checkMovieDetails(
                        title = "Inception",
                        releaseDate = "2010-07-16",
                        overview = "A thief who steals corporate secrets through dream-sharing technology.",
                        rating = "8.8",
                    )
                }
            } finally {
                scenario.close()
            }
        }
    }

    /** Проверяет ошибочный сценарий: при сбое загрузки деталей фильма
     * показывается сообщение об ошибке и можно вернуться назад. */
    @Test
    fun detailScreen_showsNetworkErrorAndReturnsBackToHome() {
        run {
            fakeUserRepository.makeMovieDetailFailWithNetworkError()
            val scenario = launchActivity<ComposeActivity>()
            try {
                step("Открываем главный экран и переходим к фильму с ошибкой загрузки деталей") {
                    movieFlowActions.openMovieFromHome(movieId = "movie_1", title = "Inception")
                }

                step("Проверяем отображение ошибочного сценария на экране деталей") {
                    movieFlowActions.checkNetworkErrorOnDetailScreen()
                    movieFlowActions.closeErrorDialog()
                }

                step("Возвращаемся назад и убеждаемся, что снова видим главный экран") {
                    movieFlowActions.returnToHomeFromDetailScreen()
                }
            } finally {
                scenario.close()
            }
        }
    }

    private inline fun <reified T : ComponentActivity> launchActivity(): ActivityScenario<T> {
        return ActivityScenario.launch(T::class.java)
    }
}


