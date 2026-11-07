package com.nexo.ui.settings
import com.nexo.dto.UserSettingsDTO

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: UserSettingsDTO) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}