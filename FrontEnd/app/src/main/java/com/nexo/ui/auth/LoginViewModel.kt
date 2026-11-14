package com.nexo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AuthApiService
import com.nexo.dto.LoginUserDTO
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    val isFormValid: Boolean
        get() {
            val e = email.value.trim()
            val p = password.value
            return android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() && p.isNotBlank()
        }

    fun login() {
        if (_uiState.value is LoginUiState.Loading) return

        val trimmedEmail = email.value.trim()
        val rawPassword = password.value

        if (trimmedEmail.isBlank()) {
            _uiState.value = LoginUiState.Error(AppStrings.errEmailBlankLogin)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = LoginUiState.Error(AppStrings.errEmailInvalid)
            return
        }
        if (rawPassword.isBlank()) {
            _uiState.value = LoginUiState.Error(AppStrings.errPasswordBlankLogin)
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val request = LoginUserDTO(email = trimmedEmail, password = rawPassword)
                val response = authApiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    tokenManager.saveTokens(
                        access = loginResponse.accessToken,
                        refresh = loginResponse.refreshToken
                    )
                    _uiState.value = LoginUiState.Success
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.takeIf { it.isNotBlank() }
                        ?: AppStrings.errInvalidCredentials

                    _uiState.value = LoginUiState.Error(errorMessage)
                }
            } catch (e: IOException) {
                _uiState.value = LoginUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = LoginUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: AppStrings.errUnexpected)
            }
        }
    }

    fun resetUiState() {
        _uiState.value = LoginUiState.Idle
    }
}