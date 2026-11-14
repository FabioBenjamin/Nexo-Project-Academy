package com.nexo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AuthApiService
import com.nexo.dto.LoginUserDTO
import com.nexo.security.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.nexo.ui.theme.AppStrings

class LoginViewModel(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Encapsulation: Private MutableStateFlow, public StateFlow
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // State update events delegated to the ViewModel
    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun login() {
        if (username.value.isBlank() || password.value.isBlank()) {

            _uiState.value = LoginUiState.Error(AppStrings.errFillFields)
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val request = LoginUserDTO(username = username.value, password = password.value)
                val response = authApiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    tokenManager.saveTokens(access = loginResponse.accessToken, refresh = loginResponse.refreshToken)
                    _uiState.value = LoginUiState.Success
                } else {

                    _uiState.value = LoginUiState.Error(AppStrings.errInvalidCredentials)
                }
            } catch (e: Exception) {

                _uiState.value = LoginUiState.Error(e.message ?: AppStrings.errServerConn)
            }
        }
    }

    fun resetUiState() {
        _uiState.value = LoginUiState.Idle
    }
}