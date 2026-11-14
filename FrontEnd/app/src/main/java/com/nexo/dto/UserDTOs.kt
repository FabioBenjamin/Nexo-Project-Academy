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
    val achievements: List<AchievementDTO>
)

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
