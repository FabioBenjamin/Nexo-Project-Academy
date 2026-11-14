package com.nexo.dto

import com.google.gson.annotations.SerializedName

data class UserAchievementDTO(
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