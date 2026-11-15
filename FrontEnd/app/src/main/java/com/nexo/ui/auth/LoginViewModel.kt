package com.nexo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AuthApiService
import com.nexo.data.api.UserApiService
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
    private val userApiService: UserApiService,
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

    private fun isValidUserIdentifier(input: String): Boolean {
        if (input.isBlank()) return false
        return if (input.contains("@")) {
            android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
        } else {
            input.length >= 3 && !input.contains(" ")
        }
    }

    val isFormValid: Boolean
        get() {
            val u = email.value.trim()
            val p = password.value
            return isValidUserIdentifier(u) && p.isNotBlank()
        }

    fun login() {
        if (_uiState.value is LoginUiState.Loading) return

        val trimmedUser = email.value.trim()
        val rawPassword = password.value

        if (trimmedUser.isBlank()) {
            _uiState.value = LoginUiState.Error(AppStrings.errEmailBlankLogin)
            return
        }
        if (!isValidUserIdentifier(trimmedUser)) {
            _uiState.value = LoginUiState.Error(
                if (trimmedUser.contains("@")) AppStrings.errEmailInvalid else AppStrings.errUsernameLength
            )
            return
        }
        if (rawPassword.isBlank()) {
            _uiState.value = LoginUiState.Error(AppStrings.errPasswordBlankLogin)
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val request = LoginUserDTO(user = trimmedUser, password = rawPassword)
                val response = authApiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    tokenManager.saveTokens(
                        access = loginResponse.accessToken,
                        refresh = loginResponse.refreshToken
                    )

                    val profileResponse = userApiService.getUserProfile()
                    if (profileResponse.isSuccessful && profileResponse.body() != null) {
                        tokenManager.saveUserId(profileResponse.body()!!.id)
                    }

                    _uiState.value = LoginUiState.Success
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