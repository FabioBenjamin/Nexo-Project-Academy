package com.nexo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.UserApiService
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
                    val errorMsg = response.errorBody()?.string()
                    val finalMessage = errorMsg?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errLoadProfile} (${response.code()})"

                    _uiState.value = ProfileUiState.Error(finalMessage)
                }
            } catch (e: IOException) {
                _uiState.value = ProfileUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = ProfileUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: AppStrings.errUnexpected)
            }
        }
    }
}