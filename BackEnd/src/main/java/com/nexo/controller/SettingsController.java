package com.nexo.controller;

import com.nexo.dto.SettingsDTO;
import com.nexo.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/setrtings")
@CrossOrigin(origins = "*") // Enable access for frontend/mobile (CORS)
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    // Retrieves user settings
    @GetMapping("/{userId}")
    public ResponseEntity<SettingsDTO> getSettings(@PathVariable Long userId) {
        return ResponseEntity.ok(settingsService.getByUserId(userId));
    }

    // Updates the settings sent in the JSON
    @PutMapping("/{userId}")
    public ResponseEntity<SettingsDTO> updateSettings(@PathVariable Long userId, @RequestBody SettingsDTO settingsDTO) {
        return ResponseEntity.ok(settingsService.update(userId, settingsDTO));
    }
}
