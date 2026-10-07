package com.nexo.models.classes;

import com.nexo.models.enums.AchievementRequirementType;
import jakarta.persistence.*;

@Entity
@Table(name = "achievementrequirement")
public class AchievementRequirement {

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

    public Long getId() {
        return id;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public AchievementRequirementType getType() {
        return type;
    }

    public Integer getTarget() {
        return target;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public void setType(AchievementRequirementType type) {
        this.type = type;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }
}
