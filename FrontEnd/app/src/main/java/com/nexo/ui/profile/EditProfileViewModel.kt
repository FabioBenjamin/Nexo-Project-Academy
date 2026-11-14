package com.nexo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.UserApiService
import com.nexo.dto.UserProfileResponse
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class EditProfileViewModel(
    private val userApiService: UserApiService,
    initialUsername: String,
    initialEmail: String
) : ViewModel() {

    private val _username = MutableStateFlow(initialUsername)
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow(initialEmail)
    val email: StateFlow<String> = _email.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updateNewPassword(password: String) {
        _newPassword.value = password
    }

    fun updateConfirmPassword(password: String) {
        _confirmPassword.value = password
    }

    val isFormValid: Boolean
        get() {
            val u = username.value.trim()
            val e = email.value.trim()
            val np = newPassword.value
            val cp = confirmPassword.value

            val isUsernameValid = u.length >= 3
            val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()

            // Se o campo de senha foi preenchido, valida se tem 6+ caracteres e se bate com a confirmação
            val isPasswordValid = if (np.isNotBlank()) {
                np.length >= 6 && np == cp
            } else {
                true // Senha vazia significa que o usuário optou por não alterá-la
            }

            return isUsernameValid && isEmailValid && isPasswordValid
        }

    fun saveChanges() {
        val trimmedUsername = username.value.trim()
        val trimmedEmail = email.value.trim()
        val np = newPassword.value
        val cp = confirmPassword.value

        if (trimmedUsername.length < 3) {
            _uiState.value = EditProfileUiState.Error(AppStrings.errUsernameLength)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = EditProfileUiState.Error(AppStrings.errEmailInvalid)
            return
        }
        if (np.isNotBlank()) {
            if (np.length < 6) {
                _uiState.value = EditProfileUiState.Error(AppStrings.errPasswordLength)
                return
            }
            if (np != cp) {
                _uiState.value = EditProfileUiState.Error(AppStrings.errPasswordMismatchSpecific)
                return
            }
        }

        if (_uiState.value is EditProfileUiState.Loading) return

        _uiState.value = EditProfileUiState.Loading

        viewModelScope.launch {
            try {
                // Requisição para atualizar o perfil no backend
                val response = userApiService.updateProfile(
                    UserProfileResponse(
                        id = 0L,
                        username = trimmedUsername,
                        email = trimmedEmail,
                        password = if (np.isNotBlank()) np else null, // Ou string vazia "" dependendo do seu DTO
                        level = 0,
                        currentXp = 0,
                        maxXp = 0,
                        completedTrailsCount = 0,
                        achievements = emptyList()
                    )
                )

                if (response.isSuccessful) {
                    _uiState.value = EditProfileUiState.Success
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errServer} (${response.code()})"
                    _uiState.value = EditProfileUiState.Error(errorMessage)
                }
            } catch (e: IOException) {
                _uiState.value = EditProfileUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = EditProfileUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error(e.message ?: AppStrings.errUnexpected)
            }
        }
    }

    fun resetUiState() {
        _uiState.value = EditProfileUiState.Idle
    }
}