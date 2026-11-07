package com.example.nexoacademy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoacademy.data.api.AuthApiService
import com.example.nexoacademy.data.dto.LoginUserDTO
import com.example.nexoacademy.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    var username = MutableStateFlow("")
    var password = MutableStateFlow("")

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login() {
        if (username.value.isBlank() || password.value.isBlank()) {
            _uiState.value = LoginUiState.Error("Preencha todos os campos")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val request = LoginUserDTO(
                    username = username.value,
                    password = password.value
                )

                val response = authApiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    // Salva os tokens no DataStore via TokenManager
                    tokenManager.saveTokens(
                        access = loginResponse.accessToken,
                        refresh = loginResponse.refreshToken
                    )

                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error("Usuário ou senha inválidos")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Erro ao conectar com o servidor")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = LoginUiState.Idle
    }
}