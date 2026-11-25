package com.nexo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.SettingsApiService
import com.nexo.dto.UserSettingsDTO
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppPreferencesState
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsApiService: SettingsApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadSettings()
    }

    private suspend fun reconcileSettingsWithDevice(remote: UserSettingsDTO) {
        val localTheme = tokenManager.getTheme()
        val localLanguage = tokenManager.getLanguage()

        val shouldPushSystemToRemote =
            (localTheme.equals("system", ignoreCase = true) && !remote.theme.equals("system", ignoreCase = true)) ||
                    (localLanguage.equals("system", ignoreCase = true) && !remote.language.equals("system", ignoreCase = true))

        if (shouldPushSystemToRemote) {
            val syncedSettings = remote.copy(
                theme = if (localTheme.equals("system", ignoreCase = true)) "system" else remote.theme,
                language = if (localLanguage.equals("system", ignoreCase = true)) "system" else remote.language
            )
            AppPreferencesState.updateFromSettings(syncedSettings.theme, syncedSettings.language)
            tokenManager.saveCachedSettings(syncedSettings)
            _uiState.value = SettingsUiState.Success(syncedSettings)

            try {
                settingsApiService.updateSettings(syncedSettings)
            } catch (_: Exception) {
            }
        } else {
            AppPreferencesState.updateFromSettings(remote.theme, remote.language)
            tokenManager.saveCachedSettings(remote)
            _uiState.value = SettingsUiState.Success(remote)
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            tokenManager.getCachedSettings()?.let { cached ->
                AppPreferencesState.updateFromSettings(cached.theme, cached.language)
                _uiState.value = SettingsUiState.Success(cached)
            }

            try {
                val response = settingsApiService.getSettings()
                if (response.isSuccessful && response.body() != null) {
                    reconcileSettingsWithDevice(response.body()!!)
                } else if (_uiState.value !is SettingsUiState.Success) {
                    _uiState.value = SettingsUiState.Error("${AppStrings.errServer} (${response.code()})")
                }
            } catch (e: Exception) {
                if (_uiState.value !is SettingsUiState.Success) {
                    _uiState.value = SettingsUiState.Error(AppStrings.errUnexpected)
                }
            }
        }
    }

    fun updateSettings(newSettings: UserSettingsDTO) {
        val previousSettings = (_uiState.value as? SettingsUiState.Success)?.settings

        AppPreferencesState.updateFromSettings(newSettings.theme, newSettings.language)
        _uiState.value = SettingsUiState.Success(newSettings)

        viewModelScope.launch {
            _isSaving.value = true
            tokenManager.saveCachedSettings(newSettings)
            try {
                val response = settingsApiService.updateSettings(newSettings)
                if (response.isSuccessful && response.body() != null) {
                    val saved = response.body()!!
                    AppPreferencesState.updateFromSettings(saved.theme, saved.language)
                    tokenManager.saveCachedSettings(saved)
                    _uiState.value = SettingsUiState.Success(saved)
                } else if (previousSettings != null) {
                    _uiState.value = SettingsUiState.Success(newSettings)
                }
            } catch (_: Exception) {
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val response = settingsApiService.getSettings()
                if (response.isSuccessful && response.body() != null) {
                    reconcileSettingsWithDevice(response.body()!!)
                }
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}