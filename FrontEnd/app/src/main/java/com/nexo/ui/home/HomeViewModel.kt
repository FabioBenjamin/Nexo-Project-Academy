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
import com.nexo.ui.theme.AppStrings
import com.nexo.data.api.UserApiService
import kotlinx.coroutines.async

class HomeViewModel(
    private val trailApiService: TrailApiService,
    private val authApiService: AuthApiService,
    private val userApiService: UserApiService,
    private val tokenManager: TokenManager
) : ViewModel() {
    constructor(
        trailApiService: TrailApiService,
        authApiService: AuthApiService,
        userApiService: TokenManager
    ) : this()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTrails()
    }

    fun loadTrails(page: Int = 0) {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                val trailsDeferred = async { trailApiService.getTrails(page) }
                val profileDeferred = async { userApiService.getUserProfile() }

                val trailsResponse = trailsDeferred.await()
                val profileResponse = profileDeferred.await()

                if (trailsResponse.isSuccessful && trailsResponse.body() != null) {
                    val trails = trailsResponse.body()!!.content ?: emptyList()
                    val profile = if (profileResponse.isSuccessful) profileResponse.body() else null
                    _uiState.value = HomeUiState.Success(trails = trails, profile = profile)
                } else if (trailsResponse.code() == 401) {
                    _uiState.value = HomeUiState.Error(AppStrings.errSessionExpired)
                } else {
                    val errorMsg = trailsResponse.errorBody()?.string()
                    val finalMessage = errorMsg?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errLoadTrails} (${trailsResponse.code()})"

                    _uiState.value = HomeUiState.Error(finalMessage)
                }
            } catch (e: IOException) {
                _uiState.value = HomeUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = HomeUiState.Error(AppStrings.errServer)
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
}