package com.nexo.models.classes;

import jakarta.persistence.*;

@Entity
@Table(name="settings")
public class Settings {

    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "settings_id")
    private Long id;

    @ManyToOne // N:1
    @JoinColumn(name = "user_id", unique = true) // Find FK
    private User user;

    @Column(name="notifications", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean notifications;

    @Column(name = "language", columnDefinition = "VARCHAR(10)")
    private String language = "pt-BR";

    @Column(length = 10, columnDefinition = "DEFAULT 'light'", nullable = false)
    private String theme = "light";

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean studyWeekends;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Boolean getNotifications() {
        return notifications;
    }

    public String getLanguage() {
        return language;
    }

    public Boolean getStudyWeekends() {
        return studyWeekends;
    }

    public String getTheme() { return theme; }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setStudyWeekends(Boolean studyWeekends) {
        this.studyWeekends = studyWeekends;
    }

    public void setTheme(String theme) { this.theme = theme; }
}