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

            if (_uiState.value !is HomeUiState.Success) {
                _uiState.value = HomeUiState.Loading
            }
            fetchFirstPage(isPullToRefresh = false)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            currentPage = 0
            isLastPage = false
            isFetchingNextPage = false
            _isRefreshing.value = true
            fetchFirstPage(isPullToRefresh = true)
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchFirstPage(isPullToRefresh: Boolean) {
        try {
            val trailsResponse = trailApiService.getTrails(page = 0)
            val profileResponse = try {
                userApiService.getUserProfile()
            } catch (_: Exception) {
                null
            }

            if (trailsResponse.isSuccessful && trailsResponse.body() != null) {
                val pageBody = trailsResponse.body()!!
                val profile = if (profileResponse?.isSuccessful == true) profileResponse.body() else null

                if (profile != null) {
                    tokenManager.saveUserId(profile.id)
                    tokenManager.updateStudiedToday(profile.studiedToday)
                }

                currentPage = pageBody.number
                isLastPage = pageBody.last

                _uiState.value = HomeUiState.Success(
                    trails = pageBody.content,
                    profile = profile,
                    isLoadingMore = false,
                    isLastPage = isLastPage
                )
            } else if (trailsResponse.code() == 401) {
                tokenManager.clearTokens()
                _uiState.value = HomeUiState.Unauthorized
            } else if (!isPullToRefresh) {
                _uiState.value = HomeUiState.Error("${AppStrings.errLoadTrails} (${trailsResponse.code()})")
            }
        } catch (e: IOException) {
            if (!isPullToRefresh) _uiState.value = HomeUiState.Error(AppStrings.errNetwork)
        } catch (e: HttpException) {
            if (!isPullToRefresh) _uiState.value = HomeUiState.Error(AppStrings.errServer)
        } catch (e: Exception) {
            if (!isPullToRefresh) _uiState.value = HomeUiState.Error(e.message ?: AppStrings.errUnexpected)
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