package com.nexo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDTO {
    private Integer id;
    private String title;
    private String description;
    private Integer xpReward;
    private Boolean unlocked; // Blockead trails
    private LocalDate unlockAt; // Blockead active
    private Integer currentProgress;
    private Integer targetProgress;
}