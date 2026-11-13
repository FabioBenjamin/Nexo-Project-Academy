package com.nexo.ui.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AchievementApiService
import com.nexo.data.api.TrailApiService
import com.nexo.dto.StudyTrailResponse
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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

    fun loadTrailDetails() {
        viewModelScope.launch {
            _uiState.value = TrailDetailsUiState.Loading
            try {
                val response = trailApiService.getTrailDetails(trailId)
                if (response.isSuccessful && response.body() != null) {
                    val trail = response.body()!!
                    val progress = calculateProgress(trail)
                    _uiState.value = TrailDetailsUiState.Success(trail = trail, progressFraction = progress)
                } else {
                    _uiState.value = TrailDetailsUiState.Error("${AppStrings.errLoadTrailDetails} (${response.code()})")
                }
            } catch (e: IOException) {
                _uiState.value = TrailDetailsUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = TrailDetailsUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                _uiState.value = TrailDetailsUiState.Error(AppStrings.errUnexpected)
            }
        }
    }

    fun completeTopic(topicId: Long) {
        viewModelScope.launch {
            try {
                val response = trailApiService.completeTopic(topicId)
                if (response.isSuccessful) {
                    val userId = tokenManager.getUserId() ?: 1L
                    val achievementResponse = achievementApiService.completeActivity(userId)

                    if (achievementResponse.isSuccessful) {
                        loadTrailDetails()
                    } else {
                        _uiState.value = TrailDetailsUiState.Error(AppStrings.errLoadXp)
                    }
                } else {
                    _uiState.value = TrailDetailsUiState.Error("${AppStrings.errConcludeTopic} (${response.code()})")
                }
            } catch (e: IOException) {
                _uiState.value = TrailDetailsUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = TrailDetailsUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                _uiState.value = TrailDetailsUiState.Error(e.message ?: AppStrings.errUnexpected)
            }
        }
    }

    private fun calculateProgress(trail: StudyTrailResponse): Float {
        val allTopicsCount = trail.topics.sumOf { 1 + it.subTopics.size }
        val completedCount = trail.topics.sumOf { topic ->
            val mainCompleted = if (topic.status.equals("COMPLETED", ignoreCase = true)) 1 else 0
            val subCompleted = topic.subTopics.count { it.status.equals("COMPLETED", ignoreCase = true) }
            mainCompleted + subCompleted
        }
        return if (allTopicsCount > 0) completedCount.toFloat() / allTopicsCount else 0f
    }
}