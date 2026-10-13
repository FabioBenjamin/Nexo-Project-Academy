package com.nexo.models.classes;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

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


    // Default Constructor
    public User() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public Integer getMaxStreak() {
        return maxStreak;
    }

    public void setMaxStreak(Integer maxStreak) {
        this.maxStreak = maxStreak;
    }

    // LEAGUE SYSTEM
    // Helper method to calculate the League dynamically based on the streak days
    public String getLeague() {
        if (currentStreak >= 365) return "Lendário";
        if (currentStreak >= 100) return "Diamante";
        if (currentStreak >= 30) return "Ouro";
        if (currentStreak >= 7) return "Prata";
        if (currentStreak >= 3) return "Bronze";

        // For users who are already losing thier streak
        // Or new users
        return "Rocha"; // Initial league
    }
}