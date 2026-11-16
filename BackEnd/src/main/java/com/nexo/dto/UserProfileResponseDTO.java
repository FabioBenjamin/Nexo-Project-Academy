package com.nexo.dto;

import java.util.List;

public class UserProfileResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Integer level;
    private Integer currentXp;
    private Integer maxXp;
    private Integer totalXp;
    private Integer currentStreak;
    private Integer maxStreak;
    private Integer completedActivitiesCount;
    private Long totalTrailsCount;
    private Long completedTrailsCount;
    private boolean studiedToday;
    private List<AchievementDTO> achievements;

    // Construtor
    public UserProfileResponseDTO(
            Long id,
            String username,
            String email,
            Integer level,
            Integer currentXp,
            Integer maxXp,
            Integer totalXp,
            Integer currentStreak,
            Integer maxStreak,
            Integer completedActivitiesCount,
            Long totalTrailsCount,
            Long completedTrailsCount,
            boolean studiedToday,
            List<AchievementDTO> achievements
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.level = level;
        this.currentXp = currentXp;
        this.maxXp = maxXp;
        this.totalXp = totalXp;
        this.currentStreak = currentStreak;
        this.maxStreak = maxStreak;
        this.completedActivitiesCount = completedActivitiesCount;
        this.totalTrailsCount = totalTrailsCount;
        this.completedTrailsCount = completedTrailsCount;
        this.studiedToday = studiedToday;
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

    public Integer getTotalXp() {
        return totalXp;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public Integer getMaxStreak() {
        return maxStreak;
    }

    public Integer getCompletedActivitiesCount() {
        return completedActivitiesCount;
    }

    public Long getTotalTrailsCount() {
        return totalTrailsCount;
    }

    public Long getCompletedTrailsCount() {
        return completedTrailsCount;
    }

    public boolean isStudiedToday() {
        return studiedToday;
    }

    public List<AchievementDTO> getAchievements() {
        return achievements;
    }
}
