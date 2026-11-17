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

    fun loadSettings() {
        viewModelScope.launch {
            // 1. Cache-First: exibe instantaneamente o que está no DataStore
            val cached = tokenManager.getCachedSettings()
            if (cached != null) {
                AppPreferencesState.updateFromSettings(cached.theme, cached.language)
                _uiState.value = SettingsUiState.Success(cached)
            } else {
                _uiState.value = SettingsUiState.Loading
            }

            // 2. Busca o estado mais recente no BackEnd (GET /settings)
            try {
                val response = settingsApiService.getSettings()
                if (response.isSuccessful && response.body() != null) {
                    val remote = response.body()!!
                    AppPreferencesState.updateFromSettings(remote.theme, remote.language)
                    tokenManager.saveCachedSettings(remote)
                    _uiState.value = SettingsUiState.Success(remote)
                } else if (cached == null) {
                    _uiState.value = SettingsUiState.Error("${AppStrings.errServer} (${response.code()})")
                }
            } catch (_: Exception) {
                if (cached == null) {
                    _uiState.value = SettingsUiState.Error(AppStrings.errNetwork)
                }
            }
        }
    }
    fun updateSettings(newSettings: UserSettingsDTO) {
        val previousSettings = (_uiState.value as? SettingsUiState.Success)?.settings

        // Atualização otimista imediata (UI + Tema/Idioma + Cache Local)
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
                    // Mantém o estado local caso o servidor rejeite
                    _uiState.value = SettingsUiState.Success(newSettings)
                }
            } catch (_: Exception) {
                // Mantém salvo no cache local mesmo offline
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
                    val saved = response.body()!!
                    AppPreferencesState.updateFromSettings(saved.theme, saved.language)
                    tokenManager.saveCachedSettings(saved)
                    _uiState.value = SettingsUiState.Success(saved)
                }
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}