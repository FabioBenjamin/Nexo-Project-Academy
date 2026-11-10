package com.nexo.dto;

import java.util.List;

public class UserProfileResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Integer level;
    private Integer currentXp;
    private Integer maxXp;
    private Integer currentStreak;
    private List<AchievementDTO> achievements;

    // Construtor
    public UserProfileResponseDTO(
            Long id,
            String username,
            String email,
            Integer level,
            Integer currentXp,
            Integer maxXp,
            Integer currentStreak,
            List<AchievementDTO> achievements
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.level = level;
        this.currentXp = currentXp;
        this.maxXp = maxXp;
        this.currentStreak = currentStreak;
        this.achievements = achievements;
    }

    // Getters


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getCurrentXp() {
        return currentXp;
    }

    public Integer getMaxXp() {
        return maxXp;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public List<AchievementDTO> getAchievements() {
        return achievements;
    }
}
