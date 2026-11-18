package com.nexo.ui.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AchievementApiService
import com.nexo.data.api.TrailApiService
import com.nexo.data.api.UserApiService
import com.nexo.dto.StudyTrailResponse
import com.nexo.dto.UpdateStudyTrailRequest
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
    private val userApiService: UserApiService,
    private val achievementApiService: AchievementApiService,
    private val tokenManager: TokenManager,
    private val trailId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrailDetailsUiState>(TrailDetailsUiState.Loading)
    val uiState: StateFlow<TrailDetailsUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _rewardEvent = MutableStateFlow<GamificationRewardEvent?>(null)
    val rewardEvent: StateFlow<GamificationRewardEvent?> = _rewardEvent.asStateFlow()

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading.asStateFlow()

    private val _trailDeleted = MutableStateFlow(false)
    val trailDeleted: StateFlow<Boolean> = _trailDeleted.asStateFlow()

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    fun clearActionError() {
        _actionError.value = null
    }

    init {
        loadTrailDetails()
    }

    fun dismissRewardEvent() {
        _rewardEvent.value = null
    }

    fun loadTrailDetails() {
        viewModelScope.launch {
            // 1. Cache-First: exibe os detalhes salvos localmente de imediato
            val cached = tokenManager.getCachedTrailDetails(trailId)
            if (cached != null) {
                _uiState.value = TrailDetailsUiState.Success(
                    trail = cached,
                    progressFraction = calculateProgress(cached)
                )
            } else if (_uiState.value !is TrailDetailsUiState.Success) {
                _uiState.value = TrailDetailsUiState.Loading
            }

            // 2. Busca os dados mais recentes da API
            try {
                val response = trailApiService.getTrailDetails(trailId)
                if (response.isSuccessful && response.body() != null) {
                    val trail = response.body()!!
                    tokenManager.saveCachedTrailDetails(trail)
                    _uiState.value = TrailDetailsUiState.Success(
                        trail = trail,
                        progressFraction = calculateProgress(trail)
                    )
                } else if (cached == null) {
                    _uiState.value = TrailDetailsUiState.Error("${AppStrings.errLoadTrailDetails} (${response.code()})")
                }
            } catch (e: IOException) {
                if (cached == null) _uiState.value = TrailDetailsUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                if (cached == null) _uiState.value = TrailDetailsUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                if (cached == null) _uiState.value = TrailDetailsUiState.Error(AppStrings.errUnexpected)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val response = trailApiService.getTrailDetails(trailId)
                if (response.isSuccessful && response.body() != null) {
                    val trail = response.body()!!
                    tokenManager.saveCachedTrailDetails(trail)
                    _uiState.value = TrailDetailsUiState.Success(
                        trail = trail,
                        progressFraction = calculateProgress(trail)
                    )
                }
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun completeTopic(topicId: Long) {
        viewModelScope.launch {
            try {
                val beforeProfileResp = userApiService.getUserProfile()
                val beforeProfile = if (beforeProfileResp.isSuccessful) beforeProfileResp.body() else null
                val userId = beforeProfile?.id ?: tokenManager.getUserId()

                if (userId != null) {
                    tokenManager.saveUserId(userId)
                }

                val response = trailApiService.completeTopic(topicId)
                if (!response.isSuccessful) {
                    _uiState.value = TrailDetailsUiState.Error("${AppStrings.errConcludeTopic} (${response.code()})")
                    return@launch
                }

                val updatedTrailResp = trailApiService.getTrailDetails(trailId)
                var isTestNowUnlocked = false
                var unlockedTestId: Long? = null
                var unlockedTestPassingScore = 7

                if (updatedTrailResp.isSuccessful && updatedTrailResp.body() != null) {
                    val updatedTrail = updatedTrailResp.body()!!
                    tokenManager.saveCachedTrailDetails(updatedTrail)
                    val newProgress = calculateProgress(updatedTrail)
                    _uiState.value = TrailDetailsUiState.Success(trail = updatedTrail, progressFraction = newProgress)

                    val testPreview = updatedTrail.studyTestPreviewResponse
                    if (newProgress >= 1.0f && testPreview != null && testPreview.studyTestStatus.equals("OPEN", ignoreCase = true)) {
                        isTestNowUnlocked = true
                        unlockedTestId = testPreview.id
                        unlockedTestPassingScore = testPreview.passingScore
                    }
                }

                val afterProfileResp = userApiService.getUserProfile()
                val afterProfile = if (afterProfileResp.isSuccessful) afterProfileResp.body() else null

                if (afterProfile != null) {
                    tokenManager.saveCachedProfile(afterProfile)
                    tokenManager.updateStudiedToday(afterProfile.studiedToday)
                }

                if (beforeProfile != null && afterProfile != null) {
                    val beforeTotalXp = (beforeProfile.level - 1) * beforeProfile.maxXp + beforeProfile.currentXp
                    val afterTotalXp = (afterProfile.level - 1) * afterProfile.maxXp + afterProfile.currentXp
                    val xpGained = (afterTotalXp - beforeTotalXp).coerceAtLeast(50)

                    val beforeUnlockedIds = beforeProfile.achievements
                        .filter { it.unlocked }
                        .map { it.id }
                        .toSet()

                    val newlyUnlocked = afterProfile.achievements.filter {
                        it.unlocked && it.id !in beforeUnlockedIds
                    }

                    _rewardEvent.value = GamificationRewardEvent(
                        xpGained = xpGained,
                        leveledUp = afterProfile.level > beforeProfile.level,
                        newLevel = afterProfile.level,
                        trailCompleted = false,
                        testUnlocked = isTestNowUnlocked,
                        unlockedTestId = unlockedTestId,
                        unlockedTestPassingScore = unlockedTestPassingScore,
                        unlockedAchievements = newlyUnlocked
                    )
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

    fun updateTrail(newTitle: String, newDescription: String, onUpdated: () -> Unit) {
        val trimmedTitle = newTitle.trim()
        val trimmedDescription = newDescription.trim()

        if (trimmedTitle.isEmpty() || trimmedDescription.isEmpty()) {
            _actionError.value = AppStrings.editTrailEmptyError
            return
        }

        viewModelScope.launch {
            _isActionLoading.value = true
            _actionError.value = null
            try {
                val response = trailApiService.updateTrail(
                    trailId = trailId,
                    request = UpdateStudyTrailRequest(
                        title = trimmedTitle,
                        description = trimmedDescription
                    )
                )

                if (response.isSuccessful) {
                    val refreshed = trailApiService.getTrailDetails(trailId)
                    if (refreshed.isSuccessful && refreshed.body() != null) {
                        val trail = refreshed.body()!!
                        tokenManager.saveCachedTrailDetails(trail)
                        _uiState.value = TrailDetailsUiState.Success(
                            trail = trail,
                            progressFraction = calculateProgress(trail)
                        )
                    }
                    onUpdated()
                } else {
                    _actionError.value = "${AppStrings.errServer} (${response.code()})"
                }
            } catch (e: IOException) {
                _actionError.value = AppStrings.errNetwork
            } catch (e: Exception) {
                _actionError.value = e.message ?: AppStrings.errUnexpected
            } finally {
                _isActionLoading.value = false
            }
        }
    }

    fun deleteTrail() {
        viewModelScope.launch {
            _isActionLoading.value = true
            _actionError.value = null
            try {
                val response = trailApiService.deleteTrail(trailId)
                if (response.isSuccessful) {
                    tokenManager.removeCachedTrailDetails(trailId)
                    // Remove também do cache da lista da Home para não piscar antes do reload
                    tokenManager.getCachedHomeTrails()?.let { cachedList ->
                        tokenManager.saveCachedHomeTrails(cachedList.filterNot { it.id == trailId })
                    }
                    _trailDeleted.value = true
                } else {
                    _actionError.value = "${AppStrings.errServer} (${response.code()})"
                }
            } catch (e: IOException) {
                _actionError.value = AppStrings.errNetwork
            } catch (e: Exception) {
                _actionError.value = e.message ?: AppStrings.errUnexpected
            } finally {
                _isActionLoading.value = false
            }
        }
    }
}