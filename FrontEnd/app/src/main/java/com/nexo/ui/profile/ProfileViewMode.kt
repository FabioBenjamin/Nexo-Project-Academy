package com.nexo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.UserApiService
import com.nexo.dto.UserProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val profile: UserProfileResponse) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val userApiService: UserApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                val response = userApiService.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProfileUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProfileUiState.Error("Erro ao carregar o perfil (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Falha de rede ou erro inesperado.")
            }
        }
    }
}