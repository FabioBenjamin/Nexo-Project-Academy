package com.nexo.dto

// Request DTO for creating a learning path via Gemini
data class StudyTrailRequestDTO(
    val content: String,
    val targetDate: String // Format YYYY-MM-DD
)

// Summary DTO for listing on the Dashboard (/trails)
data class StudyTrailSummaryResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val status: String // IN_PROGRESS, PENDING, COMPLETED
)

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val last: Boolean,
    val number: Int
)