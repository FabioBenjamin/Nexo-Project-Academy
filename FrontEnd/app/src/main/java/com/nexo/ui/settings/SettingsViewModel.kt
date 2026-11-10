package com.nexo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.SettingsApiService
import com.nexo.dto.UserSettingsDTO
import com.nexo.ui.theme.AppPreferencesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsApiService: SettingsApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        _uiState.value = SettingsUiState.Loading
        viewModelScope.launch {
            try {
                val response = settingsApiService.getSettings()
                if (response.isSuccessful && response.body() != null) {
                    val saved = response.body()!!
                    AppPreferencesState.updateFromSettings(saved.theme, saved.language)
                    _uiState.value = SettingsUiState.Success(response.body()!!)
                } else {
                    _uiState.value = SettingsUiState.Error("Erro ao carregar as configurações (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Falha de rede ou erro inesperado.")
            }
        }
    }

    fun updateSettings(newSettings: UserSettingsDTO) {
        // Atualiza visualmente na hora antes mesmo da resposta da API
        AppPreferencesState.updateFromSettings(newSettings.theme, newSettings.language)
        _uiState.value = SettingsUiState.Loading
        viewModelScope.launch {
            try {
                val response = settingsApiService.updateSettings(newSettings)
                if (response.isSuccessful && response.body() != null) {
                    val saved = response.body()!!
                    AppPreferencesState.updateFromSettings(saved.theme, saved.language)
                    _uiState.value = SettingsUiState.Success(response.body()!!)
                } else {
                    _uiState.value = SettingsUiState.Error("Erro ao salvar as configurações (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Falha de rede ou erro inesperado ao salvar.")
            }
        }
    }
}