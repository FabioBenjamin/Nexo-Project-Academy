package com.nexo.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.security.TokenManager
import kotlinx.coroutines.delay
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
        checkActiveSession()
    }

    private fun checkActiveSession() {
        viewModelScope.launch {
            delay(600) // Tempo breve para exibir a identidade visual do Nexo
            val access = tokenManager.accessToken.firstOrNull()
            val refresh = tokenManager.refreshToken.firstOrNull()
            val hasSession = !access.isNullOrBlank() || !refresh.isNullOrBlank()

            _destination.value = if (hasSession) {
                SplashDestination.GoToHome
            } else {
                SplashDestination.GoToLogin
            }
        }
    }
}