package com.nexo.controller;

import com.nexo.dto.AchievementDTO;
import com.nexo.dto.UserProfileResponseDTO;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.models.classes.User;
import com.nexo.repository.UserRepository;
import com.nexo.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final AchievementService achievementService;

    // Contrutor
    public UserController(UserRepository userRepository, AchievementService achievementService) {
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getCurrentUserProfile(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        int totalXp = user.getXp() != null ? user.getXp() : 0;

        // Cálculo simples de nível a cada 300 XP
        int xpPerLevel = 300;
        int level = (totalXp / xpPerLevel) + 1; //
        int currentXpInLevel = totalXp % xpPerLevel; //

        List<AchievementDTO> achievements = achievementService.getUserAchievements(userId);

        UserProfileResponseDTO response = new UserProfileResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                level,
                currentXpInLevel,
                xpPerLevel,
                user.getCurrentStreak() != null ? user.getCurrentStreak() : 0,
                achievements
        );

        return ResponseEntity.ok(response);
    }
}
