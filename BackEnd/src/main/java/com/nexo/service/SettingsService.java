package com.nexo.service;

import com.nexo.dto.SettingsDTO;
import com.nexo.models.classes.Settings;
import com.nexo.models.classes.User;
import com.nexo.repository.SettingsRepository;
import com.nexo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    public SettingsDTO getByUserId(Long userId) {
        Settings settings = (Settings) settingsRepository.findByUserId(userId).orElseGet(() -> (Settings) createDefaultSettings(userId));
        return mapToDTO(settings);

    }

    public SettingsDTO update(Long userId, SettingsDTO dto) {
        Settings settings = (Settings) settingsRepository.findByUserId(userId)
                .orElseGet(() -> (Settings) createDefaultSettings(userId));

        // User choicces in the Settings

        if (dto.getNotifications() != null) {
            settings.setNotifications(dto.getNotifications());
        }

        if (dto.getLanguage() != null) {
            settings.setLanguage(dto.getLanguage());
        }

        if (dto.getTheme() != null) {
            settings.setTheme(dto.getTheme());
        }

        if (dto.getStudyWeekends() != null) {
            settings.setStudyWeekends(dto.getStudyWeekends());
        }

        // Save user changes
        Settings update = settingsRepository.save(settings);
        return mapToDTO(update);
    }

    private UserRepository createDefaultSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Settings settings = new Settings();
        settings.setUser(user);
        settings.setNotifications(false);
        settings.setLanguage("pt-BR");
        settings.setTheme("light");
        settings.setStudyWeekends(true);
        return null;
    }

    private SettingsDTO mapToDTO(Settings settings) {
        return new SettingsDTO(
                settings.getNotifications(),
                settings.getLanguage(),
                settings.getTheme(),
                settings.getStudyWeekends()
        );
    }
}
