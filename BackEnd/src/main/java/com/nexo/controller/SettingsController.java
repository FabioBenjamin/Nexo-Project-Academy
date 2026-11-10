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
}
