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

    // Default Constructor
    public User() {}

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