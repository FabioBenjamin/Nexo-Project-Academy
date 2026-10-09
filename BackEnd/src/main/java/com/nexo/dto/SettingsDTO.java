package com.nexo.dto;

public class SettingsDTO {
    private Boolean notifications;
    private String language;
    private String theme;
    private Boolean studyWeekends;

    public SettingsDTO() {}

    public SettingsDTO(Boolean notifications, String language, String theme, Boolean studyWeekends) {
        this.notifications = notifications;
        this.language = language;
        this.theme = theme;
        this.studyWeekends = studyWeekends;
    }

    public Boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getStudyWeekends() {
        return studyWeekends;
    }

    public void setStudyWeekends(Boolean studyWeekends) {
        this.studyWeekends = studyWeekends;
    }
}
