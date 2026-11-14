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
    val achievements: List<AchievementDTO>,
    val password: String
) {
    constructor(
        id: Long,
        username: String,
        email: String,
        password: String?,
        level: Int,
        currentXp: Int,
        maxXp: Int,
        completedTrailsCount: Int,
        achievements: List<AchievementDTO>
    ) : this(
        id,
        username,
        email,
        level,
        currentXp,
        maxXp,
        completedTrailsCount,
        achievements,
        password
    )
}

// DTO representing an individual achievement badge
data class AchievementDTO(
    val id: Long,
    val title: String,
    val description: String,
    val xpReward: Int,
    val isUnlocked: Boolean,
    val completedAt: String? = null
)
