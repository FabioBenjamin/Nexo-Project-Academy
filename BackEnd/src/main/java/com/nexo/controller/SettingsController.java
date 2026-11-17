package com.nexo.controller;

import com.nexo.dto.LearningProfileRequestDTO;
import com.nexo.dto.SettingsDTO;
import com.nexo.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings") //
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

    @PatchMapping("/learning/profile")
    public ResponseEntity<Void> updateLearningProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody LearningProfileRequestDTO request) {

        Long userId = Long.valueOf(jwt.getSubject());

        settingsService.updateLearningProfile(userId, request.learningProfile());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/onboarding/skip")
    public ResponseEntity<Void> skipOnboardingQuiz(
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = Long.valueOf(jwt.getSubject());
        settingsService.skipOnboardingQuiz(userId);

        return ResponseEntity.ok().build();
    }
}
