package com.nexo.dto

import androidx.tracing.TrackDescriptor

data class StudyTestPreviewResponse(
    val id: Long,
    val studyTestStatus: String, // "BLOCKED", "OPEN", "COMPLETED"
    val passingScore: Int,
    val userScore: Int? = null
)

data class StudyTestResponse(
    val questions: List<QuestionResponse>
)

data class QuestionResponse(
    val id: Long,
    val statement: String,
    val alternatives: List<AlternativeResponse>
)

data class AlternativeResponse(
    val id: Long,
    val description: String
)

data class AnswerRequest(
    val questionId: Long,
    val alternativeId: Long
)

data class TestAttemptResultRequest(
    val answers: List<AnswerRequest>
)

data class QuestionExplanationResponse(
    val id: Long,
    val explanation: String,
    val isCorrect: Boolean
)

data class TestAttemptResultResponse(
    val questionExplanations: List<QuestionExplanationResponse>,
    val score: Int,
    val approved: Boolean
)