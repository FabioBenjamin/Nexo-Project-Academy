package com.nexo.ui.trail

import com.nexo.dto.UserAchievementDTO

data class GamificationRewardEvent(
    val xpGained: Int,
    val leveledUp: Boolean,
    val newLevel: Int,
    val trailCompleted: Boolean,
    val unlockedAchievements: List<UserAchievementDTO>
)