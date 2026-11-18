package com.nexo.models.classes;

import com.nexo.models.enums.AchievementRequirementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "achievementrequirement")
public class AchievementRequirement {

    // Getters and Setters
    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "requirement_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "achievement_id") // FK
    private Achievement achievement;

    @Enumerated(EnumType.STRING)
    private AchievementRequirementType type;

    @Column(nullable = false)
    private Integer target;

    public AchievementRequirement() {}

    public AchievementRequirement(Achievement achievement, AchievementRequirementType type, Integer target) {
        this.achievement = achievement;
        this.type = type;
        this.target = target;
    }
}
