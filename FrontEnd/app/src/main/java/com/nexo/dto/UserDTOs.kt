package com.nexo.dto

// User profile response containing level, XP progress, and unlocked achievements
data class UserProfileResponse(
    val id: Long,
    val username: String,
    val email: String,
    val level: Int,
    val currentXp: Int,
    val maxXp: Int,
    val completedTrailsCount: Int,
    val achievements: List<String>
)

// DTO representing an individual achievement badge
data class AchievementDTO(
    val id: Long,
    val title: String,
    val description: String,
    val xpReward: Int,
    val isUnlocked: Boolean,
    val completedAt: String? = null
)
