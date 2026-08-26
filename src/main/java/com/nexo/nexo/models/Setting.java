package com.nexo.nexo.models;

@Entity
@Table(name="settings")
public class Setting {

    @Id // tell: this attribute is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This attribute is generate automatically
    // every time attribute is different to tablename name, must specify tb_name in @Column
    @Column(name = "topicprogress_id")
    private Long id;

    @ManyToOne // N:1
    @JoinColumn(name = "user_id") // Find FK
    private User user;

    @Column(name="notifications", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean notifications;

    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'pt-BR'")
    private String language;

    // TODO: modelar coluna theme

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
}
