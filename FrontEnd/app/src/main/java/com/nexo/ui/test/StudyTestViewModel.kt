package com.nexo.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.StudyTestApiService
import com.nexo.dto.AnswerRequest
import com.nexo.dto.TestAttemptResultRequest
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppStrings
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
    private val tokenManager: TokenManager
): ViewModel() {

    private val _uiState = MutableStateFlow<StudyTestUiState>(StudyTestUiState.Loading)
    val uiState: StateFlow<StudyTestUiState> = _uiState.asStateFlow()

    val requiredScore: Int get() = passingScore

    init {
        loadTest()
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
                    val questions = response.body()!!.questions

                    _uiState.value = StudyTestUiState.InProgress(
                        questions = questions,
                        selectedAnswers = emptyMap()
                    )
                } else {
                    val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank()}
                        ?: "Erro ao carregar prova (${response.code()})"
                    _uiState.value = StudyTestUiState.Error(errorMsg)
                }
            }catch (e: IOException) {
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
            _uiState.value = current.copy(
                submitError = "Responda todas as questões antes de enviar."
            )
            return
        }

        _uiState.value = current.copy(isSubmitting = true, submitError = null)

        viewModelScope.launch {
            try {
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
                    _uiState.value = StudyTestUiState.Result(
                        questions = current.questions,
                        selectedAnswers = current.selectedAnswers,
                        result = response.body()!!
                    )
                } else {
                    val errorMsg = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                        ?: "Erro ao enviar respostas (${response.code()})"
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