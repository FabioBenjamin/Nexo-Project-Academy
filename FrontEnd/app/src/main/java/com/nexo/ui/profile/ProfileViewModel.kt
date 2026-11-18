package com.nexo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.UserApiService
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProfileViewModel(
    private val userApiService: UserApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val cached = tokenManager.getCachedProfile()
            if (cached != null) {
                _uiState.value = ProfileUiState.Success(cached)
            } else {
                _uiState.value = ProfileUiState.Loading
            }

            try {
                val response = userApiService.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    tokenManager.saveCachedProfile(profile)
                    _uiState.value = ProfileUiState.Success(profile)
                } else if (cached == null) {
                    val errorMsg = response.errorBody()?.string()
                    val finalMessage = errorMsg?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errLoadProfile} (${response.code()})"
                    _uiState.value = ProfileUiState.Error(finalMessage)
                }
            } catch (e: IOException) {
                if (cached == null) _uiState.value = ProfileUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                if (cached == null) _uiState.value = ProfileUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                if (cached == null) _uiState.value = ProfileUiState.Error(e.message ?: AppStrings.errUnexpected)
            }
        }
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val response = userApiService.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    tokenManager.saveCachedProfile(profile)
                    _uiState.value = ProfileUiState.Success(profile)
                }
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}