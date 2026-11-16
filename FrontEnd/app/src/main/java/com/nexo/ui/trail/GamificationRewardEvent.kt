package com.nexo.ui.trail

import com.nexo.dto.UserAchievementDTO

data class GamificationRewardEvent(
    val xpGained: Int,
    val leveledUp: Boolean,
    val newLevel: Int,
    val trailCompleted: Boolean = false,
    val testUnlocked: Boolean = false,
    val unlockedTestId: Long? = null,
    val unlockedTestPassingScore: Int = 7,
    val unlockedAchievements: List<UserAchievementDTO>
)