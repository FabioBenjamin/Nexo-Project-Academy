package com.nexo.ui.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.AchievementApiService
import com.nexo.data.api.TrailApiService
import com.nexo.data.api.UserApiService
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

    init {
        loadTrailDetails()
    }

    fun dismissRewardEvent() {
        _rewardEvent.value = null
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

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val response = trailApiService.getTrailDetails(trailId)
                if (response.isSuccessful && response.body() != null) {
                    val trail = response.body()!!
                    val progress = calculateProgress(trail)
                    _uiState.value = TrailDetailsUiState.Success(trail = trail, progressFraction = progress)
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
                // 1. Estado anterior do usuário (para calcular XP ganho, level up e novas conquistas)
                val beforeProfileResp = userApiService.getUserProfile()
                val beforeProfile = if (beforeProfileResp.isSuccessful) beforeProfileResp.body() else null
                val userId = beforeProfile?.id ?: tokenManager.getUserId()

                if (userId != null) {
                    tokenManager.saveUserId(userId)
                }

                // 2. Marca o tópico como concluído (o BackEnd já processa +50 XP e streak)
                val response = trailApiService.completeTopic(topicId)
                if (!response.isSuccessful) {
                    _uiState.value = TrailDetailsUiState.Error("${AppStrings.errConcludeTopic} (${response.code()})")
                    return@launch
                }

                // 3. Recarrega a trilha para verificar se atingiu 100%
                val updatedTrailResp = trailApiService.getTrailDetails(trailId)
                var isTrailNowCompleted = false

                if (updatedTrailResp.isSuccessful && updatedTrailResp.body() != null) {
                    val updatedTrail = updatedTrailResp.body()!!
                    val newProgress = calculateProgress(updatedTrail)
                    _uiState.value = TrailDetailsUiState.Success(trail = updatedTrail, progressFraction = newProgress)

                    if (newProgress >= 1.0f && userId != null) {
                        isTrailNowCompleted = true
                    }
                }

                // 4. Compara o perfil atualizado para disparar o feedback visual com o mascote
                val afterProfileResp = userApiService.getUserProfile()
                val afterProfile = if (afterProfileResp.isSuccessful) afterProfileResp.body() else null

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
                        trailCompleted = isTrailNowCompleted,
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
}