package com.nexo.dto

// User profile response
data class UserProfileResponse(
    val id: Long,
    val username: String,
    val email: String,
    val level: Int,
    val currentXp: Int,
    val maxXp: Int,
    val completedTrailsCount: Int = 0,
    val currentStreak: Int = 0,
    val studiedToday: Boolean = false,
    val achievements: List<UserAchievementDTO>,
    val password: String? = null
)


// DTOs para alteração individual de dados do usuário (alinhados ao Back-End)
data class ChangeUsernameRequest(val username: String)
data class ChangeUserEmailRequest(val email: String)
data class ChangePasswordRequest(
        val oldPassword: String,
        val newPassword: String,
        val confirmNewPassword: String
    )
