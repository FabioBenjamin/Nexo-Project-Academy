package com.nexo.dto

data class UserAchievementDTO(
    val id: Long,
    val title: String,
    val description: String?,
    val xpReward: Int,
    val unlocked: Boolean,
    val unlockAt: String?,
    val currentProgress: Int,
    val targetProgress: Int
)