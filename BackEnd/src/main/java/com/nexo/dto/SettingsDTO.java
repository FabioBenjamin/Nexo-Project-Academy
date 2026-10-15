package com.nexo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SettingsDTO {
    private Boolean notifications;
    private String language;
    private String theme;
    private Boolean studyWeekends;
    private String email;

    public SettingsDTO() {}

    // Construtor complete
    public SettingsDTO(Boolean notifications, String language, String theme, Boolean studyWeekends, String email) {
        this.notifications = notifications;
        this.language = language;
        this.theme = theme;
        this.studyWeekends = studyWeekends;
        this.email = email;
    }
}
