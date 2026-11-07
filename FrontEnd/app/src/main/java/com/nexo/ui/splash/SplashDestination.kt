package com.nexo.ui.splash

sealed interface SplashDestination {
    object Loading : SplashDestination
    object GoToHome : SplashDestination
    object GoToLogin : SplashDestination
}