package com.example.moviedb.ui.screen.splash

sealed class SplashViewState {
    data object Idle : SplashViewState()
    data object NavigateToHome : SplashViewState()
}