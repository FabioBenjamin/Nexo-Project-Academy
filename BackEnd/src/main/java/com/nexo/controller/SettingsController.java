package com.nexo.controller;

import com.nexo.dto.SettingsDTO;
import com.nexo.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings") // Alterar nome da rota
@CrossOrigin(origins = "*") // Enable access for frontend/mobile (CORS)
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    // Retrieves user settings
    @GetMapping
    public ResponseEntity<SettingsDTO> getSettings(
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(settingsService.getSettings(userId));
    }

    // Updates the settings sent in the JSON
    @PutMapping
    public ResponseEntity<SettingsDTO> updateSettings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody SettingsDTO settingsDTO) {

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(settingsService.update(userId, settingsDTO));
    }

    // Adicione dentro do seu SettingsController.java (com @RequestMapping("/settings"))

    @PatchMapping("/learning-profile")
    public ResponseEntity<Void> updateLearningProfile(
            @RequestBody LearningProfileRequestDTO request,
            Authentication authentication) {

        UserSettings settings = settingsRepository.findByUser_Email(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Configurações não encontradas"));

        settings.setLearningProfile(request.learningProfile());
        settings.setHasSeenOnboardingPrompt(true);
        settingsRepository.save(settings);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/onboarding/skip")
    public ResponseEntity<Void> skipOnboardingQuiz(Authentication authentication) {
        UserSettings settings = settingsRepository.findByUser_Email(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Configurações não encontradas"));

        settings.setHasSeenOnboardingPrompt(true);
        settingsRepository.save(settings);

        return ResponseEntity.ok().build();
    }
}
