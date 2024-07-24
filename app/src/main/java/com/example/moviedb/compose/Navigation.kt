package com.example.moviedb.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviedb.compose.ui.detail.DetailScreen
import com.example.moviedb.compose.ui.home.HomeScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(route = Screen.Main.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(navArgument(Screen.MovieDetail.Args.MOVIE_ID) {
                type = NavType.Companion.StringType
            })
        ) { backStackEntry ->
            DetailScreen(
                navController = navController,
                movieId = backStackEntry.arguments?.getString(Screen.MovieDetail.Args.MOVIE_ID)
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object MovieDetail : Screen("movieDetail/{${Args.MOVIE_ID}}") {
        object Args {
            const val MOVIE_ID = "movieId"
        }
    }
}

fun NavController.toMovieDetail(movieId: String?) {
    navigate(
        Screen.MovieDetail.route.replace(
            "{${Screen.MovieDetail.Args.MOVIE_ID}}",
            movieId ?: ""
        )
    )
}