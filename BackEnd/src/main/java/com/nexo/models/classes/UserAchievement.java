package com.nexo.models.classes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
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

    // Default Construtor
    public UserAchievement() {}

    // Convienience Construtor

    public UserAchievement(User user, Achievement achievement) {
        this.user = user;
        this.achievement = achievement;
        this.id = new UserAchievementID();
        this.completedAt = LocalDate.now();

        if(user != null) this.id.setUserId(user.getId());
        if(achievement !=  null) this.id.setAchievementId(achievement.getId());
    }

}
