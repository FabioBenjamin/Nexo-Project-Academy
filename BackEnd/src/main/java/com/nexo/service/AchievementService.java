package com.nexo.service;

import com.nexo.dto.AchievementDTO;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.models.classes.Achievement;
import com.nexo.models.classes.User;
import com.nexo.repository.AchievementRepository;
import com.nexo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Saves the last active date before updating the streak
        LocalDate previousActiveDate = user.getLastActiveDate();

        // 1. Updates streak and checks streak/league achievements
        updateStreak(user, today);

        // 2. Increments total completed activities count for the user
        int currentCount = user.getCompletedActivitiesCount() != null ? user.getCompletedActivitiesCount() : 0;
        user.setCompletedActivitiesCount(currentCount + 1);

        // 3. Time check (before 6:00 AM or after 10:00 PM) - Night Owl
        boolean isNightShift = now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(6, 0));
        if (isNightShift) {
            unlockAchievementIfNotPresent(user, 5L); // ID 5: Night Owl
        }

        // 4. First Steps: complete the 1st activity
        if (user.getCompletedActivitiesCount() >= 1) {
            unlockAchievementIfNotPresent(user, 1L); // ID 1: First Steps
        }

        // 5. Total activity count milestones (25, 50, 100...)
        checkActivityMilestones(user);

        // 6. Weekend streak check (Saturday and Sunday)
        checkWeekendStreak(user, today, previousActiveDate);

        // Saves user updates
        userRepository.save(user);
    }

    @Transactional
    public void addXpCheckAchievement(Long userId, Integer xp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        int currentXp = user.getXp() != null ? user.getXp() : 0;
        int addedXp = xp != null ? xp : 0;
        user.setXp(currentXp + addedXp);

        // Checks accumulated XP milestones (1, 500, 1000, 5000, 10000, 25000, 50000, 100000)
        checkXpMilestones(user);

        // Saves
        userRepository.save(user);
    }

    @Transactional
    public void processTrailCompletion(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        // Achievement for completing a trail
        unlockAchievementIfNotPresent(user, 13L);

        userRepository.save(user);
    }

    public List<AchievementDTO> getUserAchievements(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        List<Achievement> allAchievements = achievementRepository.findAll();
        List<Achievement> unlockedAchievements = achievementRepository.findUnlockedByUserId(userId);

        return allAchievements.stream().map(achievement -> {
            boolean isUnlocked = unlockedAchievements.stream()
                    .anyMatch(unlocked -> unlocked.getId().equals(achievement.getId()));

            // Calculation and declaration of progress variables
            int targetProgress = getTargetForAchievement(achievement.getId());
            int currentProgress = isUnlocked ? targetProgress : getCurrentProgressForAchievement(user, achievement.getId());

            return new AchievementDTO(
                    achievement.getId(),
                    achievement.getTitle(),
                    achievement.getDescription(),
                    achievement.getXpReward(),
                    isUnlocked,
                    isUnlocked ? LocalDate.now() : null,
                    currentProgress,
                    targetProgress
            );
        }).collect(Collectors.toList());
    }

    private void updateStreak(User user, LocalDate today) {
        int currentStreak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
        int maxStreak = user.getMaxStreak() != null ? user.getMaxStreak() : 0;

        if (user.getLastActiveDate() == null) {
            user.setCurrentStreak(1);
        } else if (user.getLastActiveDate().equals(today.minusDays(1))) {
            user.setCurrentStreak(currentStreak + 1);
        } else if (!user.getLastActiveDate().equals(today)) {
            user.setCurrentStreak(1);
        }

        if (user.getCurrentStreak() > maxStreak) {
            user.setMaxStreak(user.getCurrentStreak());
        }

        user.setLastActiveDate(today);

        // Leagues and streak achievement checks (Days)
        // Rock: 0 days | Bronze: 3 | Silver: 7 | Gold: 30 | Diamond: 100 | Legendary: 365
        int streak = user.getCurrentStreak();
        if (streak >= 3) unlockAchievementIfNotPresent(user, 6L);   // Bronze - 3 days
        if (streak >= 7) unlockAchievementIfNotPresent(user, 7L);   // Silver - 7 days
        if (streak >= 30) unlockAchievementIfNotPresent(user, 8L);  // Gold - 30 days
        if (streak >= 100) unlockAchievementIfNotPresent(user, 9L); // Diamond - 100 days
        if (streak >= 365) unlockAchievementIfNotPresent(user, 10L);// Legendary - 365 days
    }

    // Checks the activities completed by the user
    private void checkActivityMilestones(User user) {
        int count = user.getCompletedActivitiesCount() != null ? user.getCompletedActivitiesCount() : 0;
        if (count >= 25) unlockAchievementIfNotPresent(user, 2L);
        if (count >= 50) unlockAchievementIfNotPresent(user, 3L);
        if (count >= 100) unlockAchievementIfNotPresent(user, 4L);
    }

    // Checks the XP accumulated by the user
    private void checkXpMilestones(User user) {
        int xp = user.getXp() != null ? user.getXp() : 0;
        if (xp >= 500) unlockAchievementIfNotPresent(user, 15L);
        if (xp >= 1000) unlockAchievementIfNotPresent(user, 16L);
        if (xp >= 5000) unlockAchievementIfNotPresent(user, 17L);
        if (xp >= 10000) unlockAchievementIfNotPresent(user, 18L);
        if (xp >= 25000) unlockAchievementIfNotPresent(user, 19L);
        if (xp >= 50000) unlockAchievementIfNotPresent(user, 20L);
        if (xp >= 100000) unlockAchievementIfNotPresent(user, 21L);
    }

    private void checkWeekendStreak(User user, LocalDate today, LocalDate previousActiveDate) {
        // If the activity takes place on Sunday and the user was also active on Saturday
        if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            if (previousActiveDate != null && previousActiveDate.equals(today.minusDays(1))) {
                unlockAchievementIfNotPresent(user, 11L); // ID 11: Weekend Warrior
            }
        }
    }

    public void unlockAchievementIfNotPresent(User user, Long achievementId) {
        boolean alreadyUnlocked = achievementRepository.existsByUserIdAndAchievementId(user.getId(), achievementId);

        if (!alreadyUnlocked) {
            Achievement achievement = achievementRepository.findById(achievementId).orElse(null);
            if (achievement != null) {
                int currentXp = user.getXp() != null ? user.getXp() : 0;
                user.setXp(currentXp + achievement.getXpReward());
                userRepository.insertUserAchievement(user.getId(), achievement.getId());

                // First Achievement: unlock the user's first achievement
                if (!achievementId.equals(12L)) { // Prevents infinite recursion
                    unlockAchievementIfNotPresent(user, 12L); // ID 12: First Achievement
                }
            }
        }
    }

    // Returns the goal the user needs to reach to unlock the achievement
    private int getTargetForAchievement(Long id) {
        if (id.equals(1L)) return 1;
        if (id.equals(2L)) return 25;
        if (id.equals(3L)) return 50;
        if (id.equals(4L)) return 100;
        if (id.equals(6L)) return 3;
        if (id.equals(7L)) return 7;
        if (id.equals(8L)) return 30;
        if (id.equals(9L)) return 100;
        if (id.equals(10L)) return 365;
        if (id.equals(15L)) return 500;
        if (id.equals(16L)) return 1000;
        if (id.equals(17L)) return 5000;
        if (id.equals(18L)) return 10000;
        if (id.equals(19L)) return 25000;
        if (id.equals(20L)) return 50000;
        if (id.equals(21L)) return 100000;
        return 1;
    }

    // Returns the user's current progress based on the achievement type
    private int getCurrentProgressForAchievement(User user, Long id) {
        if (id >= 1L && id <= 4L) {
            return user.getCompletedActivitiesCount() != null ? user.getCompletedActivitiesCount() : 0;
        }
        if (id >= 6L && id <= 10L) {
            return user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
        }
        if (id >= 15L && id <= 21L) {
            return user.getXp() != null ? user.getXp() : 0;
        }
        return 0;
    }
}