package com.nexo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AuthApiService
import com.nexo.data.api.TrailApiService
import com.nexo.dto.RefreshTokenRequest
import com.nexo.security.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel(
    private val trailApiService: TrailApiService,
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTrails()
    }

    fun loadTrails(page: Int = 0) {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                val response = trailApiService.getTrails(page)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = HomeUiState.Success(response.body()!!.content)

                } else if (response.code() == 401) {
                    _uiState.value = HomeUiState.Error("Sessão expirada. Por favor, faça login novamente.")
                }

                else {
                    _uiState.value = HomeUiState.Error("Erro ao carregar as trilhas (${response.code()})")
                }

            }

            catch (e: IOException) {
                _uiState.value = HomeUiState.Error("Falha de rede. Verifique sua conexão com a internet.")
            }

            catch (e: HttpException) {
                _uiState.value = HomeUiState.Error("Erro no servidor. Tente novamente mais tarde.")
            }

            catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Ocorreu um erro inesperado.")
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            try {
                val refreshToken = tokenManager.refreshToken.firstOrNull()
                if (!refreshToken.isNullOrEmpty()) {
                    authApiService.logout(RefreshTokenRequest(refreshToken))
                }
            } catch (_: Exception) {
            } finally {
                tokenManager.clearTokens()
                onLoggedOut()
            }
        }
    }
}