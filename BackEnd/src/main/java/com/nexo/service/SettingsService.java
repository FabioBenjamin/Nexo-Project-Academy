package com.nexo.service;

import com.nexo.dto.SettingsDTO;
import com.nexo.exceptions.EmailAlreadyExistsException;
import com.nexo.models.classes.Settings;
import com.nexo.models.classes.User;
import com.nexo.repository.SettingsRepository;
import com.nexo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    public SettingsDTO getSettings(Long userId) {
        // Retrieves the settings
        // if they do not exist, creates and saves them to the database.
        Settings settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));
        return mapToDTO(settings);
    }

    @Transactional
    public SettingsDTO update(Long userId, SettingsDTO dto) {
        Settings settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // User choices in the Settings
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

    private Settings createDefaultSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Settings settings = new Settings();
        settings.setUser(user);
        settings.setNotifications(false);
        settings.setLanguage("pt-BR");
        settings.setTheme("light");
        settings.setStudyWeekends(true);

        // Saves and returns the created Settings object
        return settingsRepository.save(settings);
    }

    private SettingsDTO mapToDTO(Settings settings) {
        User user = settings.getUser();

        SettingsDTO settingsDTO =  new SettingsDTO();

        settingsDTO.setLanguage(settings.getLanguage());
        settingsDTO.setNotifications(settings.getNotifications());
        settingsDTO.setStudyWeekends(settings.getStudyWeekends());
        settingsDTO.setTheme(settings.getTheme());

        return settingsDTO;
    }

}