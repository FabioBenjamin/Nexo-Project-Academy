package com.nexo.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.security.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


class SplashViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            val accessToken = tokenManager.accessToken.firstOrNull()
            val refreshToken = tokenManager.refreshToken.firstOrNull()

            // The interceptor and the authenticator will handle the actual renewal during navigation
            // Here, we only validate whether the session exists locally
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                _destination.value = SplashDestination.GoToLogin
            } else {
                _destination.value = SplashDestination.GoToHome
            }
        }
    }
}