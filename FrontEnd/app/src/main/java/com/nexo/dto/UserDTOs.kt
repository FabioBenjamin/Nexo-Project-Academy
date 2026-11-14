package com.nexo.dto


// User profile response containing level, XP progress, and unlocked achievements
data class UserProfileResponse(
    val id: Long,
    val username: String,
    val email: String,
    val level: Int,
    val currentXp: Int,
    val maxXp: Int,
    val completedTrailsCount: Int = 0,
    val achievements: List<AchievementDTO>,
    val password: String? = null
)


