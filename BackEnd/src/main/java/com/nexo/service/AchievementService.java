package com.nexo.service;

import com.nexo.dto.AchievementDTO;
import com.nexo.models.classes.Achievement;
import com.nexo.models.classes.User;
import com.nexo.repository.AchievementRepository;
import com.nexo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void processActivityCompletion(Long userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

    LocalDate today =  LocalDate.now();
    LocalTime now = LocalTime.now();

    // Offensive update (Streak)
    udpateStreak(user, today);

    // Checks for "Night Owl" activity (before 6:00 AM or after 10:00 PM)
    boolean isNightShift = now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(6, 0));
    if (isNightShift) {
        // Business rule specific to counting nighttime activities (if necessary)
        System.out.println("Night shift activated" + user.getId());
    }

    userRepository.save(user);

    }

    private void udpateStreak(User user, LocalDate today) {
        if (user.getLastActiveDate() == null) {
            // The offensive begins in 1
            user.setCurrentStreak(1);
        }
        else if (user.getLastActiveDate().equals(today.minusDays(1))) {
            // Activity performed the following day (works for Saturdays, Sundays, and weekdays): increments the streak.
            user.setCurrentStreak(user.getCurrentStreak() + 1);
        }
        else if (!user.getLastActiveDate().equals(today)) {
            // Reset the streak to 1 if it is broken
            user.setCurrentStreak(1);
        }

        // Updates the longest historical streak (maxStreak)
        if (user.getCurrentStreak() > user.getMaxStreak()) {
            user.setMaxStreak(user.getCurrentStreak());
        }

        // Marks today's date as the last active day
        user.setLastActiveDate(today);
    }
}
