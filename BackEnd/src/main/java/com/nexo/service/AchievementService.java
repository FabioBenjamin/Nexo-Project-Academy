package com.nexo.service;

import com.nexo.dto.AchievementDTO;
import com.nexo.models.classes.Achievement;
import com.nexo.models.classes.User;
import com.nexo.repository.AchievementRepository;
import com.nexo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void processActivityCompletion(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Offensive update (Streak)
        updateStreak(user, today);

        // Checks for "Night Owl" activity (before 6:00 AM or after 10:00 PM)
        boolean isNightShift = now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(6, 0));
        if (isNightShift) {
            // Business rule specific to counting nighttime activities (if necessary)
            System.out.println("Night shift activated " + user.getId());
            unlockAchievementIfNotPresent(user, 5L);
        }

        // Unlock the "First Steps" achievement by completing the first activity
        unlockAchievementIfNotPresent(user, 1L);

        // Save
        userRepository.save(user);
    }

    @Transactional
    public void addXpCheckAchievement(Long userId, Integer xp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        user.setXp(user.getXp() + xp);

        // Check accumulated XP milestones
        if (user.getXp() >= 500) {
            unlockAchievementIfNotPresent(user, 4L);
        }

        // Save
        userRepository.save(user);
    }

    public List<AchievementDTO> getUserAchievements(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        List<Achievement> allAchievements = achievementRepository.findAll();
        List<Achievement> unlockedAchievements = achievementRepository.findUnlockedByUserId(userId);

        // Iterates through all registered achievements in the database
        return allAchievements.stream().map(achievement -> {

            // Checks if the current achievement is present in the user's unlocked list
            boolean isUnlocked = unlockedAchievements.stream()
                    .anyMatch(unlocked -> unlocked.getId().equals(achievement.getId()));

            return new AchievementDTO(
                    achievement.getId(),
                    achievement.getTitle(),
                    achievement.getDescription(),
                    achievement.getXpReward(),
                    isUnlocked,
                    isUnlocked ? LocalDate.now() : null,
                    isUnlocked ? 1 : 0, // Current user progress (1 = completed, 0 = pending)
                    1 // Target progress required to unlock
            );

        }).collect(Collectors.toList());
    }

    private void updateStreak(User user, LocalDate today) {
        if (user.getLastActiveDate() == null) {
            // The offensive begins in 1
            user.setCurrentStreak(1);
        } else if (user.getLastActiveDate().equals(today.minusDays(1))) {
            // Activity performed the following day (works for Saturdays, Sundays, and weekdays): increments the streak.
            user.setCurrentStreak(user.getCurrentStreak() + 1);
        } else if (!user.getLastActiveDate().equals(today)) {
            // Reset the streak to 1 if it is broken
            user.setCurrentStreak(1);
        }

        // Updates the longest historical streak (maxStreak)
        if (user.getCurrentStreak() > user.getMaxStreak()) {
            user.setMaxStreak(user.getCurrentStreak());
        }

        // Marks today's date as the last active day
        user.setLastActiveDate(today);

        // Check streak achievements (e.g., 3-day streak)
        if (user.getCurrentStreak() >= 3) {
            unlockAchievementIfNotPresent(user, 6L);
        }
    }

    public void unlockAchievementIfNotPresent(User user, Long achievementId) {
        boolean alreadyUnlocked = achievementRepository.existsByUserIdAndAchievementId(user.getId(), achievementId);

        if (!alreadyUnlocked) {
            Achievement achievement = achievementRepository.findById(achievementId).orElse(null);
            if (achievement != null) {

                // Rewards the user with the achievement XP
                user.setXp(user.getXp() + achievement.getXpReward());

                // Persists user-achievement association in userachievement table
                userRepository.insertUserAchievement(user.getId(), achievement.getId());

                System.out.println("Achievement unlocked " + achievement.getTitle());
            }
        }
    }
}