package com.nexo.dto;

import lombok.Getter;
import lombok.Setter;


public class SettingsDTO {
    private Boolean notifications;
    private String language;
    private String theme;
    private Boolean studyWeekends;
    private String email;

    public SettingsDTO() {}


    public Boolean getNotifications() {
        return notifications;
    }

    public String getLanguage() {
        return language;
    }

    public String getTheme() {
        return theme;
    }

    public Boolean getStudyWeekends() {
        return studyWeekends;
    }

    public String getEmail() {
        return email;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setStudyWeekends(Boolean studyWeekends) {
        this.studyWeekends = studyWeekends;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
