package com.example.nexoacademy.data.dto

// DTO de requisição para criação de trilha via Gemini
data class StudyTrailRequestDTO(
    val content: String,
    val targetDate: String // Formato YYYY-MM-DD
)

// DTO de resumo para listagem no Dashboard (/trails)
data class StudyTrailSummaryResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val status: String // IN_PROGRESS, PENDING, COMPLETED
)