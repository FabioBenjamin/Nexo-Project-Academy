package com.nexo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.R
import com.nexo.data.api.AuthApiService
import com.nexo.data.api.UserApiService
import com.nexo.dto.ChangePasswordRequest
import com.nexo.dto.ChangeUserEmailRequest
import com.nexo.dto.ChangeUsernameRequest
import com.nexo.dto.RefreshTokenRequest
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class MascotAvatarOption(
    val id: String,
    val resId: Int,
    val name: String
)

class EditProfileViewModel(
    private val userApiService: UserApiService,
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val initialUsername: String,
    private val initialEmail: String,
    initialAvatarResId: Int = R.drawable.mascote_default
) : ViewModel() {

    // Lista de poses do Mascote disponíveis para seleção
    val mascotAvatars = listOf(
        MascotAvatarOption("default", R.drawable.mascote_default, AppStrings.cdMascotDefault),
        MascotAvatarOption("focado", R.drawable.mascote_focado, AppStrings.cdMascotStudying),
        MascotAvatarOption("calma", R.drawable.mascote_calma, AppStrings.cdMascotCalm),
        MascotAvatarOption("campeao", R.drawable.mascote_campeao, AppStrings.cdMascotChampion),
        MascotAvatarOption("apoio", R.drawable.mascote_apoio, AppStrings.cdMascotSupport)
    )

    private val _selectedAvatar = MutableStateFlow(initialAvatarResId)
    val selectedAvatar: StateFlow<Int> = _selectedAvatar.asStateFlow()

    private val _username = MutableStateFlow(initialUsername)
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow(initialEmail)
    val email: StateFlow<String> = _email.asStateFlow()

    private val _oldPassword = MutableStateFlow("")
    val oldPassword: StateFlow<String> = _oldPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun updateSelectedAvatar(resId: Int) { _selectedAvatar.value = resId }
    fun updateUsername(value: String) { _username.value = value }
    fun updateEmail(value: String) { _email.value = value }
    fun updateOldPassword(value: String) { _oldPassword.value = value }
    fun updateNewPassword(value: String) { _newPassword.value = value }
    fun updateConfirmPassword(value: String) { _confirmPassword.value = value }

    // Validation flags
    val hasAvatarChange: Boolean
        get() = selectedAvatar.value != mascotAvatars.first().resId

    val hasUsernameChange: Boolean
        get() = username.value.trim() != initialUsername

    val hasEmailChange: Boolean
        get() = email.value.trim() != initialEmail

    val hasPasswordChange: Boolean
        get() = oldPassword.value.isNotBlank()

    val isUsernameValid: Boolean
        get() = !hasUsernameChange || username.value.trim().length >= 3

    val isEmailValid: Boolean
        get() = !hasEmailChange || android.util.Patterns.EMAIL_ADDRESS.matcher(email.value.trim()).matches()

    val isPasswordValid: Boolean
        get() = !hasPasswordChange || (
                newPassword.value.isNotBlank() &&
                        newPassword.value.length >= 6 &&
                        confirmPassword.value.isNotBlank() &&
                        newPassword.value == confirmPassword.value
                )

    val isFormValid: Boolean
        get() = (hasUsernameChange || hasEmailChange || hasPasswordChange || hasAvatarChange) &&
                isUsernameValid && isEmailValid && isPasswordValid

    fun saveChanges() {
        if (_uiState.value is EditProfileUiState.Loading) return

        val trimmedUsername = username.value.trim()
        val trimmedEmail = email.value.trim()
        val op = oldPassword.value
        val np = newPassword.value
        val cp = confirmPassword.value

        if (hasUsernameChange && trimmedUsername.length < 3) {
            _uiState.value = EditProfileUiState.Error(AppStrings.errUsernameLength)
            return
        }

        if (hasEmailChange && !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = EditProfileUiState.Error(AppStrings.errEmailInvalid)
            return
        }

        if (hasPasswordChange) {
            if (np.length < 6) {
                _uiState.value = EditProfileUiState.Error(AppStrings.errPasswordLength)
                return
            }
            if (np != cp) {
                _uiState.value = EditProfileUiState.Error(AppStrings.errPasswordMismatchSpecific)
                return
            }
        }

        _uiState.value = EditProfileUiState.Loading

        viewModelScope.launch {
            try {
                if (hasUsernameChange) {
                    val resp = userApiService.changeUsername(ChangeUsernameRequest(trimmedUsername))
                    if (!resp.isSuccessful) {
                        _uiState.value = EditProfileUiState.Error("${AppStrings.errUsernameChange}: ${resp.message()} (${resp.code()})")
                        return@launch
                    }
                }

                if (hasEmailChange) {
                    val resp = userApiService.changeEmail(ChangeUserEmailRequest(trimmedEmail))
                    if (!resp.isSuccessful) {
                        _uiState.value = EditProfileUiState.Error("${AppStrings.errEmailChange}: ${resp.message()} (${resp.code()})")
                        return@launch
                    }
                }

                if (hasPasswordChange) {
                    val resp = userApiService.changePassword(
                        ChangePasswordRequest(oldPassword = op, newPassword = np, confirmNewPassword = cp)
                    )
                    if (!resp.isSuccessful) {
                        _uiState.value = EditProfileUiState.Error(AppStrings.errInvalidCredentials)
                        return@launch
                    }
                }

                _uiState.value = EditProfileUiState.Success
            } catch (e: IOException) {
                _uiState.value = EditProfileUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = EditProfileUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error(e.message ?: AppStrings.errUnexpected)
            }
        }
    }

    // --- ZONA DE PERIGO: Funções de Encerramento e Exclusão ---
    fun logoutAllSessions(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val refreshToken = tokenManager.refreshToken.firstOrNull()
                if (!refreshToken.isNullOrEmpty()) {
                    authApiService.logout(RefreshTokenRequest(refreshToken))
                }
            } catch (_: Exception) {
            } finally {
                tokenManager.clearTokens()
                onSuccess()
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = EditProfileUiState.Loading
            try {
                tokenManager.clearTokens()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error("${AppStrings.errDeleteAccount}: ${e.message}")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = EditProfileUiState.Idle
    }
}