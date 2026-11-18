package com.nexo.dto

// Data Transfer Object representing user settings, aligned with Spring Boot Settings entity
data class UserSettingsDTO(
    val email: String = "",
    val notifications: Boolean = false,
    val language: String = "pt-BR",
    val theme: String = "light",
    val studyWeekends: Boolean = true,
    val learningProfile: String? = null,
    val hasSeenOnboardingPrompt: Boolean = false
)

data class LearningProfileRequest(
    val learningProfile: String
)