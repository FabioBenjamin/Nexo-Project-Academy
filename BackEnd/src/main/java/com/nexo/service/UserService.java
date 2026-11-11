package com.nexo.service;

import com.nexo.dto.*;
import com.nexo.exceptions.*;
import com.nexo.models.classes.User;
import com.nexo.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AchievementService achievementService;
    private final PasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;

    public UserService(
            UserRepository userRepository,
            AchievementService achievementService,
            PasswordEncoder passwordEncoder

    ){
        this.userRepository = userRepository;
        this.achievementService = achievementService;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = EmailValidator.getInstance();
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

    public void changeUsername(Long userId, ChangeUsernameRequest request){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Valid if new username isn't the same
        if(user.getUsername().equals(request.username())){
            throw new IllegalArgumentException("Must type a different username");
        }

        // Valid blank and null username
        if(request.username() == null || request.username().isBlank()){
            throw new IllegalArgumentException("Username must not be null");
        }

        // Verify if new username is in use
        if(userRepository.existsByUsername(request.username())){
            throw new UsernameAlreadyExistsException("Username is taken. Please choose another one");
        }

        user.setUsername(request.username());
        userRepository.save(user);
    }

    public void changeEmail(Long userId, ChangeUserEmailRequest request){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(user.getEmail().equals(request.email())){
            throw new IllegalArgumentException("Must provide a different email");
        }

        // Valid email
        if(!emailValidator.isValid(request.email())){
            throw new InvalidEmailException("Invalid Email!");
        }

        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        user.setEmail(request.email());
        userRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Verify if the passwords match
        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid Credential");
        }

        if(request.newPassword().isBlank()){
            throw new IllegalArgumentException("New password must not be null");
        }

        if(!request.newPassword().equals(request.confirmNewPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }

        String hashPassword = passwordEncoder.encode(request.newPassword());

        user.setPassword(hashPassword);
        userRepository.save(user);
    }
}
