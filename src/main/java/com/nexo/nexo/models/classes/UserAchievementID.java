package com.nexo.nexo.models.classes;


import java.io.Serializable;
import java.util.Objects;

// For understanding: This class don't represent
// a table, but a field set that will be implemented into another @Entity
public class UserAchievementID implements Serializable {

    private Long userId;
    private Long achievementId;

    public Long getUserId() {
        return userId;
    }

    public Long getAchievementId() {
        return achievementId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAchievementID)) return false;

        UserAchievementID that = (UserAchievementID) o;

        return Objects.equals(userId, that.userId)
                && Objects.equals(achievementId, that.achievementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, achievementId);
    }

}
