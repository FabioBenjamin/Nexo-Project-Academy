package com.nexo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AuthApiService
import com.nexo.data.api.TrailApiService
import com.nexo.data.api.UserApiService
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

class HomeViewModel(
    private val trailApiService: TrailApiService,
    private val userApiService: UserApiService,
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 0
    private var isLastPage = false
    private var isFetchingNextPage = false

    init {
        loadTrails()
    }

    fun loadTrails() {
        viewModelScope.launch {
            currentPage = 0
            isLastPage = false
            isFetchingNextPage = false

            // 1. Cache-First: immediately hydrates tracks and profile if they exist in the DataStore
            val cachedTrails = tokenManager.getCachedHomeTrails()
            val cachedProfile = tokenManager.getCachedProfile()

            if (cachedTrails != null) {
                _uiState.value = HomeUiState.Success(
                    trails = cachedTrails,
                    profile = cachedProfile,
                    isLoadingMore = false,
                    isLastPage = false
                )
            } else if (_uiState.value !is HomeUiState.Success) {
                _uiState.value = HomeUiState.Loading
            }

            // 2. Synchronizes with the backend in the background

            fetchFirstPage(silentFailure = cachedTrails != null)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            currentPage = 0
            isLastPage = false
            isFetchingNextPage = false
            _isRefreshing.value = true
            try {
                // In a pull-to-refresh scenario, the current screen is never destroyed in the event of a network failure.
                fetchFirstPage(silentFailure = true)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private suspend fun fetchFirstPage(silentFailure: Boolean) {
        try {
            val trailsResponse = trailApiService.getTrails(page = 0)
            val profileResponse = try {
                userApiService.getUserProfile()
            } catch (_: Exception) {
                null
            }

            if (trailsResponse.isSuccessful && trailsResponse.body() != null) {
                val pageBody = trailsResponse.body()!!
                val remoteProfile = if (profileResponse?.isSuccessful == true) profileResponse.body() else null
                val finalProfile = remoteProfile ?: tokenManager.getCachedProfile()

                if (remoteProfile != null) {
                    tokenManager.saveUserId(remoteProfile.id)
                    tokenManager.updateStudiedToday(remoteProfile.studiedToday)
                    tokenManager.saveCachedProfile(remoteProfile)
                }

                // Updates the local cache of the front page
                tokenManager.saveCachedHomeTrails(pageBody.content)

                currentPage = pageBody.number
                isLastPage = pageBody.last

                _uiState.value = HomeUiState.Success(
                    trails = pageBody.content,
                    profile = finalProfile,
                    isLoadingMore = false,
                    isLastPage = isLastPage
                )
            } else if (trailsResponse.code() == 401) {
                tokenManager.clearTokens()
                _uiState.value = HomeUiState.Unauthorized
            } else if (!silentFailure) {
                _uiState.value = HomeUiState.Error("${AppStrings.errLoadTrails} (${trailsResponse.code()})")
            }
        } catch (e: IOException) {
            if (!silentFailure) _uiState.value = HomeUiState.Error(AppStrings.errNetwork)
        } catch (e: HttpException) {
            if (!silentFailure) _uiState.value = HomeUiState.Error(AppStrings.errServer)
        } catch (e: Exception) {
            if (!silentFailure) _uiState.value = HomeUiState.Error(e.message ?: AppStrings.errUnexpected)
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        if (isLastPage || isFetchingNextPage || currentState.isLoadingMore) return

        viewModelScope.launch {
            isFetchingNextPage = true
            _uiState.value = currentState.copy(isLoadingMore = true)

            try {
                val nextPage = currentPage + 1
                val response = trailApiService.getTrails(page = nextPage)

                if (response.isSuccessful && response.body() != null) {
                    val pageBody = response.body()!!
                    currentPage = pageBody.number
                    isLastPage = pageBody.last

                    val updatedTrails = (currentState.trails + pageBody.content).distinctBy { it.id }
                    tokenManager.saveCachedHomeTrails(updatedTrails)

                    _uiState.value = currentState.copy(
                        trails = updatedTrails,
                        isLoadingMore = false,
                        isLastPage = isLastPage
                    )
                } else {
                    _uiState.value = currentState.copy(isLoadingMore = false)
                }
            } catch (_: Exception) {
                _uiState.value = currentState.copy(isLoadingMore = false)
            } finally {
                isFetchingNextPage = false
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