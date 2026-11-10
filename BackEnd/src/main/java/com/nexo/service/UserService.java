package com.nexo.service;

import com.nexo.dto.AchievementDTO;
import com.nexo.dto.UserProfileResponseDTO;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.models.classes.User;
import com.nexo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AchievementService achievementService;

    public UserService(
            UserRepository userRepository,
            AchievementService achievementService
    ){
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    public UserProfileResponseDTO getUserProfile(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        int totalXp = user.getXp() != null ? user.getXp() : 0;

        // Calculate xp level
        int xpPerLevel = 300;
        int level = (totalXp / xpPerLevel) + 1; //
        int currentXpInLevel = totalXp % xpPerLevel; //

        List<AchievementDTO> achievements = achievementService.getUserAchievements(userId);

        return new UserProfileResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                level,
                currentXpInLevel,
                xpPerLevel,
                user.getCurrentStreak() != null ? user.getCurrentStreak() : 0,
                achievements
        );
    }
}
