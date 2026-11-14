package com.nexo.dto

import com.google.gson.annotations.SerializedName

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
    val description: String?,
    val xpReward: Int,
    @SerializedName(value = "isUnlocked", alternate = ["unlocked"])
    val unlocked: Boolean = false,
    val unlockAt: String? = null,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1
)
