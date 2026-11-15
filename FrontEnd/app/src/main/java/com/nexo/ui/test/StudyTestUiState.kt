package com.nexo.ui.test

import com.nexo.dto.QuestionResponse
import com.nexo.dto.TestAttemptResultResponse

sealed interface StudyTestUiState {
    data object Loading : StudyTestUiState
    data class InProgress(
        val questions: List<QuestionResponse>,
        val selectedAnswers: Map<Long, Long>,
        val isSubmitting: Boolean = false,
        val submitError: String? = null
    ) : StudyTestUiState

    data class Result(
        val questions: List<QuestionResponse>,
        val selectedAnswers: Map<Long, Long>,
        val result: TestAttemptResultResponse
    ) : StudyTestUiState

    data class Error(val message: String) : StudyTestUiState
    data object Unauthorized : StudyTestUiState
}