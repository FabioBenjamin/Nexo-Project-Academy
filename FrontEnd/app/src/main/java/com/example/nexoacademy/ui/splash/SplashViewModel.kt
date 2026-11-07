package com.example.nexoacademy.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoacademy.data.api.AuthApiService
import com.example.nexoacademy.data.dto.RefreshTokenRequest
import com.example.nexoacademy.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


class SplashViewModel(
    private val authApiService: AuthApiService,
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

            // Se não houver tokens armazenados localmente, redireciona direto para o Login
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                _destination.value = SplashDestination.GoToLogin
                return@launch
            }

            // Tenta validar/renovar a sessão no backend utilizando o Refresh Token
            try {
                val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))

                if (response.isSuccessful && response.body() != null) {
                    val newAccessToken = response.body()!!.accessToken
                    tokenManager.saveTokens(access = newAccessToken, refresh = refreshToken)
                    _destination.value = SplashDestination.GoToHome
                } else {
                    // Token expirado ou inválido: limpa o DataStore e manda para a LoginScreen
                    tokenManager.clearTokens()
                    _destination.value = SplashDestination.GoToLogin
                }
            } catch (e: Exception) {
                // Caso haja falha de conexão no Splash, limpa a sessão local por segurança e envia ao Login
                tokenManager.clearTokens()
                _destination.value = SplashDestination.GoToLogin
            }
        }
    }
}