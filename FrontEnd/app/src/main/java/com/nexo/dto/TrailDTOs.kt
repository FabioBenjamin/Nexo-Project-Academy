package com.nexo.dto

// DTO for trail summary used in the dashboard list
data class StudyTrailSummaryResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val status: String
)

// Request DTO sent to generate a new study trail with Gemini
data class StudyTrailRequestDTO(
    val content: String,
    val targetDate: String
)


// Complete trail details response including top-level topics
data class StudyTrailResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startDate: String?,
    val targetDate: String,
    val status: String,
    val topics: List<TopicResponse>
)

// Topic and subtopic hierarchy response
data class TopicResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val targetDate: String?,
    val status: String,
    val completedAt: String?,
    val subTopics: List<TopicResponse> = emptyList()
)
