package com.nexo.controller;

import com.nexo.dto.AchievementDTO;
import com.nexo.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements") // Alterar caso for preciso
@CrossOrigin(origins = "*")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    // Retrieves all achievements with unlock status for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AchievementDTO>> getAchievements(@PathVariable Long userId) {
        List<AchievementDTO> achievements = achievementService.getUserAchievements(userId);
        return ResponseEntity.ok(achievements);
    }

    // Processes an activity completion
    // updates the user's streak and unlocks achievements
    @PostMapping("/complete-activity/{userId}")
    public ResponseEntity<Void> completeActivity(@PathVariable Long userId) {
        achievementService.processActivityCompletion(userId);
        return ResponseEntity.ok().build();
    }

    // Adds XP to a specific user and evaluates XP milestone achievements
    @PostMapping("/add-xp/{userId}")
    public ResponseEntity<Void> addXp(@PathVariable Long userId, @RequestBody Integer xp) {
        achievementService.addXpCheckAchievement(userId, xp);
        return ResponseEntity.ok().build();
    }

    // Processes a trail completion for a specific user and unlocks trail achievements
    @PostMapping("/complete-trail/{userId}")
    public ResponseEntity<Void> completeTrail(@PathVariable Long userId) {
        achievementService.processTrailCompletion(userId);
        return ResponseEntity.ok().build();
    }
}
