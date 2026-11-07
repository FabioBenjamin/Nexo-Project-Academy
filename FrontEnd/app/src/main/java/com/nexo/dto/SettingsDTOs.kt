package com.nexo.dto

// Data Transfer Object representing user settings, aligned with Spring Boot Settings entity
data class UserSettingsDTO(
    val notifications: Boolean = false,
    val language: String = "pt-BR",
    val theme: String = "light",
    val studyWeekends: Boolean = true
)