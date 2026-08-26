package com.nexo.nexo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "achievement")
public class Achievement {

    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "achievement_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(length = 50)
    private String description;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }
}
