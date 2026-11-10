package com.nexo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AuthApiService
import com.nexo.dto.RegisterUserDTO
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

    private val _uiState =
        MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register() {

        // Prevents duplicate requests
        if (_uiState.value is RegisterUiState.Loading) return

        // Validação dos campos
        if (username.value.isBlank() ||
            email.value.isBlank() ||
            password.value.isBlank() ||
            confirmPassword.value.isBlank()
        ) {
            _uiState.value = RegisterUiState.Error(
                "Preencha todos os campos obrigatórios"
            )
            return
        }

        if (password.value != confirmPassword.value) {
            _uiState.value = RegisterUiState.Error(
                "As senhas não coincidem"
            )
            return
        }

        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            try {

                val requestDTO = RegisterUserDTO(
                    username = username.value.trim(),
                    email = email.value.trim().lowercase(),
                    password = password.value
                )

                // Request to the backend
                val response = authApiService.register(requestDTO)

                if (response.isSuccessful) {

                    _uiState.value = RegisterUiState.Success

                } else {

                    val errorMessage = response.errorBody()?.string()

                    _uiState.value = RegisterUiState.Error(
                        errorMessage
                            ?.takeIf { it.isNotBlank() }
                            ?: "Erro ao cadastrar. Código: ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                _uiState.value = RegisterUiState.Error(
                    e.message ?: "Erro desconhecido ao realizar o cadastro"
                )
            }
        }
    }

    fun resetUiState() {
        _uiState.value = RegisterUiState.Idle
    }
}
