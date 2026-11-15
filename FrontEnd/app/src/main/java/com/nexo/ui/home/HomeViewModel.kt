package com.nexo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AuthApiService
import com.nexo.data.api.TrailApiService
import com.nexo.data.api.UserApiService
import com.nexo.dto.RefreshTokenRequest
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.async
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
    private val userApiService: UserApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadTrails(page: Int = 0) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val trailsDeferred = async { trailApiService.getTrails(page) }
                val profileDeferred = async { userApiService.getUserProfile() }

                val trailsResponse = trailsDeferred.await()
                val profileResponse = profileDeferred.await()

                if (trailsResponse.code() == 401 || profileResponse.code() == 401) {
                    tokenManager.clearTokens()
                    _uiState.value = HomeUiState.Error(AppStrings.errSessionExpired)
                    return@launch
                }

                if (trailsResponse.isSuccessful && trailsResponse.body() != null) {
                    val trails = trailsResponse.body()!!.content ?: emptyList()
                    val profile = if (profileResponse.isSuccessful) profileResponse.body() else null

                    if (profile != null) {
                        tokenManager.saveUserId(profile.id)
                    }

                    _uiState.value = HomeUiState.Success(trails = trails, profile = profile)
                } else {
                    val errorMsg = trailsResponse.errorBody()?.string()
                    val finalMessage = errorMsg?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errLoadTrails} (${trailsResponse.code()})"

                    _uiState.value = HomeUiState.Error(finalMessage)
                }
            } catch (e: IOException) {
                _uiState.value = HomeUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    tokenManager.clearTokens()
                    _uiState.value = HomeUiState.Error(AppStrings.errSessionExpired)
                } else {
                    _uiState.value = HomeUiState.Error(AppStrings.errServer)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: AppStrings.errUnexpected)
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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh(page: Int = 0) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val trailsDeferred = async { trailApiService.getTrails(page) }
                val profileDeferred = async { userApiService.getUserProfile() }

                val trailsResponse = trailsDeferred.await()
                val profileResponse = profileDeferred.await()

                if (trailsResponse.code() == 401 || profileResponse.code() == 401) {
                    tokenManager.clearTokens()
                    _uiState.value = HomeUiState.Error(AppStrings.errSessionExpired)
                    return@launch
                }

                if (trailsResponse.isSuccessful && trailsResponse.body() != null) {
                    val trails = trailsResponse.body()!!.content ?: emptyList()
                    val profile = if (profileResponse.isSuccessful) profileResponse.body() else null
                    if (profile != null) tokenManager.saveUserId(profile.id)

                    _uiState.value = HomeUiState.Success(trails = trails, profile = profile)
                }
            } catch (_: Exception) {
                // Em caso de falha no refresh manual, mantém os dados atuais na tela
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}