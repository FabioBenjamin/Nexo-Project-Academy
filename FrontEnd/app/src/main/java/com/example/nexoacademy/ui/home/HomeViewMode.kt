package com.example.nexoacademy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoacademy.data.api.AuthApiService
import com.example.nexoacademy.data.api.TrailApiService
import com.example.nexoacademy.data.dto.RefreshTokenRequest
import com.example.nexoacademy.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val trailApiService: TrailApiService,
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTrails()
    }

    fun loadTrails(page: Int = 0) {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            try {
                val response = trailApiService.getTrails(page)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = HomeUiState.Success(response.body()!!)
                } else if (response.code() == 401) {
                    tryRefreshTokenAndRetry(page)
                } else {
                    _uiState.value = HomeUiState.Error("Erro ao carregar as trilhas (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Falha na conexão com o servidor")
            }
        }
    }

    private suspend fun tryRefreshTokenAndRetry(page: Int) {
        val currentRefreshToken = tokenManager.refreshToken.firstOrNull()

        if (currentRefreshToken.isNullOrBlank()) {
            _uiState.value = HomeUiState.Error("Sessão expirada. Por favor, faça login novamente.")
            return
        }

        try {
            val refreshResponse = authApiService.refreshToken(RefreshTokenRequest(currentRefreshToken))
            if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newAccessToken = refreshResponse.body()!!.accessToken
                tokenManager.saveTokens(access = newAccessToken, refresh = currentRefreshToken)

                val retryResponse = trailApiService.getTrails(page)
                if (retryResponse.isSuccessful && retryResponse.body() != null) {
                    _uiState.value = HomeUiState.Success(retryResponse.body()!!)
                } else {
                    _uiState.value = HomeUiState.Error("Erro ao carregar as trilhas após renovar sessão.")
                }
            } else {
                _uiState.value = HomeUiState.Error("Sessão expirada. Por favor, faça login novamente.")
            }
        } catch (e: Exception) {
            _uiState.value = HomeUiState.Error("Falha na conexão ao renovar sessão.")
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            try {
                val refreshToken = tokenManager.refreshToken.firstOrNull()
                if (!refreshToken.isNullOrEmpty()) {
                    authApiService.logout(RefreshTokenRequest(refreshToken))
                }
            } catch (_: Exception) {
                // Ignora erro de rede para garantir que o usuário consiga sair localmente
            } finally {
                tokenManager.clearTokens()
                onLoggedOut()
            }
        }
    }
}