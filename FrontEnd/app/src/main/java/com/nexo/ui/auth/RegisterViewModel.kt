package com.nexo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AuthApiService
import com.nexo.dto.RegisterUserDTO
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authApiService: AuthApiService
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateUsername(newValue: String) { _username.value = newValue }
    fun updateEmail(newValue: String) { _email.value = newValue }
    fun updatePassword(newValue: String) { _password.value = newValue }
    fun updateConfirmPassword(newValue: String) { _confirmPassword.value = newValue }

    val isFormValid: Boolean
        get() {
            val u = username.value.trim()
            val e = email.value.trim()
            val p = password.value
            val cp = confirmPassword.value

            return u.length >= 3 &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() &&
                    p.length >= 6 &&
                    p == cp
        }
    fun register() {

        // Prevents duplicate requests
        if (_uiState.value is RegisterUiState.Loading) return

        val trimmedUsername = username.value.trim()
        val trimmedEmail = email.value.trim()
        val rawPassword = password.value
        val rawConfirmPassword = confirmPassword.value

        if (trimmedUsername.isBlank()) {
            _uiState.value = RegisterUiState.Error(AppStrings.errUsernameBlank)
            return
        }
        if (trimmedUsername.length < 3) {
            _uiState.value = RegisterUiState.Error(AppStrings.errUsernameLength)
            return
        }
        if (trimmedEmail.isBlank()) {
            _uiState.value = RegisterUiState.Error(AppStrings.errEmailBlank)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = RegisterUiState.Error(AppStrings.errEmailInvalid)
            return
        }
        if (rawPassword.isBlank()) {
            _uiState.value = RegisterUiState.Error(AppStrings.errPasswordBlank)
            return
        }
        if (rawPassword.length < 6) {
            _uiState.value = RegisterUiState.Error(AppStrings.errPasswordLength)
            return
        }
        if (rawConfirmPassword.isBlank()) {
            _uiState.value = RegisterUiState.Error(AppStrings.errConfirmPasswordBlank)
            return
        }
        if (rawPassword != rawConfirmPassword) {
            _uiState.value = RegisterUiState.Error(AppStrings.errPasswordMismatchSpecific)
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
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errServer} (${response.code()})"

                    _uiState.value = RegisterUiState.Error(errorMessage)
                }

            } catch (e: Exception) {

                _uiState.value = RegisterUiState.Error(
                    e.message ?: AppStrings.errUnexpected
                )
            }
        }
    }

    fun resetUiState() {
        _uiState.value = RegisterUiState.Idle
    }
}
