package com.nexo.nexo.models.classes;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "userachievement")
public class UserAchievement {

    @EmbeddedId
    private UserAchievementID id;

    // We don't wanna create another user_id column because it's already in UserAchievementID
    // That's why @MapsId
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    // @MapsId is saying: the user id correspond to
    // the field userId inside my composite primary key

    @ManyToOne
    @MapsId("achievementId")
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    // @MapsId is saying: the achievement id correspond to
    // the field achievementId inside my composite primary key

    @Column(name = "achievement_date", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate completedAt;

    public UserAchievementID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public void setId(UserAchievementID id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }
}
