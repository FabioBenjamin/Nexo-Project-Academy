package com.nexo.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.StudyTestApiService
import com.nexo.data.api.UserApiService
import com.nexo.dto.AnswerRequest
import com.nexo.dto.TestAttemptResultRequest
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppStrings
import com.nexo.ui.trail.GamificationRewardEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class StudyTestViewModel(
    private val testId: Long,
    private val passingScore: Int,
    private val studyTestApiService: StudyTestApiService,
    private val userApiService: UserApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudyTestUiState>(StudyTestUiState.Loading)
    val uiState: StateFlow<StudyTestUiState> = _uiState.asStateFlow()

    private val _rewardEvent = MutableStateFlow<GamificationRewardEvent?>(null)
    val rewardEvent: StateFlow<GamificationRewardEvent?> = _rewardEvent.asStateFlow()

    val requiredScore: Int get() = passingScore

    init {
        loadTest()
    }

    fun dismissRewardEvent() {
        _rewardEvent.value = null
    }

    fun loadTest() {
        _uiState.value = StudyTestUiState.Loading
        viewModelScope.launch {
            try {
                val response = studyTestApiService.getStudyTest(testId)

                if (response.code() == 401) {
                    tokenManager.clearTokens()
                    _uiState.value = StudyTestUiState.Unauthorized
                    return@launch
                }

                if (response.isSuccessful && response.body() != null) {
                    // Embaralha as alternativas mantendo o ID original de cada uma
                    val shuffledQuestions = response.body()!!.questions.map { question ->
                        question.copy(alternatives = question.alternatives.shuffled())
                    }

                    _uiState.value = StudyTestUiState.InProgress(
                        questions = shuffledQuestions,
                        selectedAnswers = emptyMap()
                    )
                } else {
                    val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errServer} (${response.code()})"
                    _uiState.value = StudyTestUiState.Error(errorMsg)
                }
            } catch (e: IOException) {
                _uiState.value = StudyTestUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = StudyTestUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                _uiState.value = StudyTestUiState.Error(e.message ?: AppStrings.errUnexpected)
            }
        }
    }

    fun selectAlternative(questionId: Long, alternativeId: Long) {
        val current = _uiState.value as? StudyTestUiState.InProgress ?: return
        if (current.isSubmitting) return

        val updatedMap = current.selectedAnswers.toMutableMap().apply {
            put(questionId, alternativeId)
        }
        _uiState.value = current.copy(
            selectedAnswers = updatedMap,
            submitError = null
        )
    }

    fun submitTest() {
        val current = _uiState.value as? StudyTestUiState.InProgress ?: return
        if (current.isSubmitting) return

        if (current.selectedAnswers.size < current.questions.size) {
            _uiState.value = current.copy(submitError = AppStrings.testErrUnanswered)
            return
        }

        _uiState.value = current.copy(isSubmitting = true, submitError = null)

        viewModelScope.launch {
            try {
                val beforeProfile = try {
                    userApiService.getUserProfile().body()
                } catch (_: Exception) {
                    null
                }

                val answers = current.questions.map { question ->
                    AnswerRequest(
                        questionId = question.id,
                        alternativeId = current.selectedAnswers.getValue(question.id)
                    )
                }

                val response = studyTestApiService.submitTestAttempt(
                    testId = testId,
                    request = TestAttemptResultRequest(answers = answers)
                )

                if (response.code() == 401) {
                    tokenManager.clearTokens()
                    _uiState.value = StudyTestUiState.Unauthorized
                    return@launch
                }

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _uiState.value = StudyTestUiState.Result(
                        questions = current.questions,
                        selectedAnswers = current.selectedAnswers,
                        result = result
                    )

                    if (result.approved) {
                        val afterProfile = try {
                            userApiService.getUserProfile().body()
                        } catch (_: Exception) {
                            null
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
                                trailCompleted = true,
                                unlockedAchievements = newlyUnlocked
                            )
                        }
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errServer} (${response.code()})"
                    _uiState.value = current.copy(isSubmitting = false, submitError = errorMsg)
                }
            } catch (e: IOException) {
                _uiState.value = current.copy(isSubmitting = false, submitError = AppStrings.errNetwork)
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    isSubmitting = false,
                    submitError = e.message ?: AppStrings.errUnexpected
                )
            }
        }
    }
}