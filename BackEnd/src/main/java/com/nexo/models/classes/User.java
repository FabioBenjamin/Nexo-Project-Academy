package com.nexo.models.classes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    // Getters and Setters
    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String password;

    // Achievement system, user-facing component

    // Start every user with the XP number
    @Column(nullable = false)
    private Integer xp = 0;

    // start for league system
    @Column(name = "current_streak" ,nullable = false)
    private Integer currentStreak = 0; // starting point

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    @Column(name = "max_streak", nullable = false)
    private Integer maxStreak = 0;

    @Column(name = "completed_activities_count", nullable = false)
    private Integer completedActivitiesCount = 0;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Settings settings;


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getXp() {
        return xp;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public Integer getMaxStreak() {
        return maxStreak;
    }

    public Integer getCompletedActivitiesCount() {
        return completedActivitiesCount;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public void setMaxStreak(Integer maxStreak) {
        this.maxStreak = maxStreak;
    }

    public void setCompletedActivitiesCount(Integer completedActivitiesCount) {
        this.completedActivitiesCount = completedActivitiesCount;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}