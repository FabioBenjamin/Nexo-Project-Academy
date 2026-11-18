package com.nexo.ui.profile
import com.nexo.dto.UserProfileResponse

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val profile: UserProfileResponse) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}