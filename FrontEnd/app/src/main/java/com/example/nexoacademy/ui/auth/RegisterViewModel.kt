package com.example.nexoacademy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoacademy.data.api.AuthApiService
import com.example.nexoacademy.data.dto.RegisterUserDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authApiService: AuthApiService
) : ViewModel() {

    var username = MutableStateFlow("")
    var email = MutableStateFlow("")
    var password = MutableStateFlow("")
    var confirmPassword = MutableStateFlow("")

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register() {
        // Validação básica local
        if (username.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
            _uiState.value = RegisterUiState.Error("Preencha todos os campos obrigatórios")
            return
        }

        if (password.value != confirmPassword.value) {
            _uiState.value = RegisterUiState.Error("As senhas não coincidem")
            return
        }

        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            try {
                val requestDTO = RegisterUserDTO(
                    username = username.value,
                    email = email.value,
                    password = password.value
                )

                // Chamada à API
                authApiService.register(requestDTO)

                _uiState.value = RegisterUiState.Success
            } catch (e: Exception) {
                // Em um cenário real, você pode tratar erros de HTTP (ex: 400, 409) especificamente
                _uiState.value = RegisterUiState.Error(e.message ?: "Erro desconhecido ao realizar o cadastro")
            }
        }
    }

    // Função para limpar o estado (útil ao sair da tela ou após exibir um Toast de erro)
    fun resetUiState() {
        _uiState.value = RegisterUiState.Idle
    }
}