package com.example.moviedb.appium

import com.example.moviedb.appium.core.BaseAppiumTest
import com.example.moviedb.appium.core.NetworkController
import com.example.moviedb.appium.pages.DetailPage
import com.example.moviedb.appium.pages.ErrorDialogPage
import com.example.moviedb.appium.pages.HomePage
import com.example.moviedb.appium.pages.SplashPage
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import org.junit.Test

@Epic("MovieDB Android")
@Feature("Black-box UI via Appium")
class MovieFlowAppiumBlackBoxTest : BaseAppiumTest() {

    @Story("Splash")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет основной сценарий: при старте приложения показывается " +
            "splash, затем открывается Home экран.")
    @Test
    fun splashScreen_isShown_thenHomeScreenOpens() {
        val splashPage = SplashPage(driver, config)
        val homePage = HomePage(driver, config)

        splashPage.waitLogoVisible()
        homePage.waitOpened()
    }

    @Story("Home")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет основной сценарий: на Home экране отображается сетка фильмов.")
    @Test
    fun homeScreen_displaysMoviesGrid() {
        val homePage = HomePage(driver, config)

        homePage.waitOpened()
        homePage.waitMoviesGridVisible()
    }

    @Story("Detail")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет основной сценарий: пользователь открывает детали " +
            "фильма и закрывает экран деталей действием Back.")
    @Test
    fun detailScreen_opensFromHome_andBackReturnsToHome() {
        val homePage = HomePage(driver, config)
        val detailPage = DetailPage(driver, config)
        val errorDialogPage = ErrorDialogPage(driver, config)

        homePage.waitOpened()
        homePage.openFirstMovieCard()

        detailPage.waitOpened()
        errorDialogPage.closeIfVisible()
        detailPage.waitBackButtonVisible()
        detailPage.tapBack()
    }

    @Story("Error handling")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Проверяет ошибочный сценарий: при отключении сети и открытии деталей" +
            " появляется диалог Network error, который можно закрыть.")
    @Test
    fun detailScreen_showsNetworkError_whenNetworkDisabled() {
        val homePage = HomePage(driver, config)
        val detailPage = DetailPage(driver, config)
        val errorDialogPage = ErrorDialogPage(driver, config)

        homePage.waitOpened()
        NetworkController.disableNetwork(config)

        try {
            homePage.openFirstMovieCard()
            detailPage.waitOpened()

            errorDialogPage.waitNetworkErrorVisible()
            errorDialogPage.closeByOk()
            errorDialogPage.assertClosed()
        } finally {
            NetworkController.enableNetwork(config)
        }
    }
}

