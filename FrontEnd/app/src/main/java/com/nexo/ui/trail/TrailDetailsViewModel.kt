package com.nexo.ui.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AchievementApiService
import com.nexo.data.api.TrailApiService
import com.nexo.security.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class TrailDetailsViewModel(
    private val trailApiService: TrailApiService,
    private val trailId: Long,
    private val achievementApiService: AchievementApiService,
    private val tokenManager: TokenManager

) : ViewModel() {

    private val _uiState = MutableStateFlow<TrailDetailsUiState>(TrailDetailsUiState.Loading)
    val uiState: StateFlow<TrailDetailsUiState> = _uiState.asStateFlow()

    init {
        loadTrailDetails()
    }

    // Loads complete study trail information and nested topics
    fun loadTrailDetails() {
        viewModelScope.launch {
            _uiState.value = TrailDetailsUiState.Loading
            try {
                val response = trailApiService.getTrailDetails(trailId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = TrailDetailsUiState.Success(response.body()!!)
                } else {
                    _uiState.value = TrailDetailsUiState.Error("Failed to load trail details (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = TrailDetailsUiState.Error("Network error. Please try again.")
            }
        }
    }

    fun completeTopic(topicId: Long) {
        viewModelScope.launch {
            try {
                val response = trailApiService.completeTopic(topicId)
                if (response.isSuccessful) {
                    val userId = tokenManager.getUserId() ?: 1L
                    achievementApiService.completeActivity(userId)
                    loadTrailDetails()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}