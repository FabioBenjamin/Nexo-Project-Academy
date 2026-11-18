package com.nexo.dto;

import java.time.LocalDate;

public record AchievementDTO(
    Long id,
    String title,
    String description,
    Integer xpReward,
    Boolean unlocked, // Blockead trails
    LocalDate unlockAt, // Blockead active
    Integer currentProgress,
    Integer targetProgress
){}