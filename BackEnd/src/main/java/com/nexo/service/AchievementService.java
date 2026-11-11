package com.nexo.service;

import com.nexo.dto.AchievementDTO;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.models.classes.Achievement;
import com.nexo.models.classes.AchievementRequirement;
import com.nexo.models.classes.User;
import com.nexo.repository.AchievementRepository;
import com.nexo.repository.AchievementRequirementRepository;
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
    private AchievementRequirementRepository achievementRequirementRepository;

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

        // 4. Weekend streak check (Saturday and Sunday)
        checkWeekendStreak(user, today, previousActiveDate);

        // 5. Evaluates database-driven achievement requirements
        checkDatabaseRequirements(user);

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

        // Evaluates accumulated XP milestones dynamically the database
        checkDatabaseRequirements(user);

        // Saves
        userRepository.save(user);
    }

    // Evaluates dynamic achievement requirements fetched from the achievementRequrement table
    private void checkDatabaseRequirements(User user) {
        List<AchievementRequirement> requirements = achievementRequirementRepository.findAll();

        for (AchievementRequirement requirement : requirements) {
            Long achievementId = requirement.getAchievement().getId();

            int target = requirement.getTarget();
            if (requirement.getType() == null) continue;

            switch (requirement.getType()) {
                case TOTAL_XP:

                    int userXp = user.getXp() != null ? user.getXp() : 0;
                    if (userXp >= target) {
                        unlockAchievementIfNotPresent(user, achievementId);
                    }
                    break;

                case COMPLETED_ACTIVITIES:
                case FIRST_STEPS:

                    // Checks completed activity count against requirement target
                    int completed = user.getCompletedActivitiesCount() != null ? user.getCompletedActivitiesCount() : 0;
                    if (completed >= target){
                        unlockAchievementIfNotPresent(user, achievementId);
                    }
                    break;

                case COMPLETE_TRAIL:
                    // COMPLETE_TRAIL should be ignored here in the general activity counter check
                    // It should be triggered via processTrailCompletion(userId) once the trail is 100% completed.
                    break;

                case STREAK_DAYS:

                    // Evaluates current active streak days count
                    int streak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
                    if (streak >= target) {
                        unlockAchievementIfNotPresent(user, achievementId);
                    }
                    break;

                default:
                    break;
            }
        }
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
    private int getTargetForAchievement(Long achievementid) {
        List<AchievementRequirement> reqs = achievementRequirementRepository.findByAchievementId(achievementid);
        if (!reqs.isEmpty()) {
            return reqs.getFirst().getTarget();
        }
        return 1;
    }

    // Returns the user's current progress based on the achievement type
    private int getCurrentProgressForAchievement(User user, Long achievementId) {
        List<AchievementRequirement> requirements = achievementRequirementRepository.findByAchievementId(achievementId);
        if (requirements.isEmpty()) {
            return 0;
        }

        // Retrieves the primary requirement associated with the achievemet
        AchievementRequirement requirement = requirements.getFirst();
        if (requirement.getType() == null) return 0;

        // Maps requirement category to corresponding user model field for progress computation
        return switch (requirement.getType()) {
            case TOTAL_XP -> user.getXp() != null ? user.getXp() : 0;
            case COMPLETED_ACTIVITIES, FIRST_STEPS, COMPLETE_TRAIL ->
                    user.getCompletedActivitiesCount() != null ? user.getCompletedActivitiesCount() : 0;

            case STREAK_DAYS -> user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
            default -> 0;
        };
    }
}