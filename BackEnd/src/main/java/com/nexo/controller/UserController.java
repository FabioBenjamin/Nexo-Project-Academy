package com.nexo.controller;

import com.nexo.dto.*;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.models.classes.User;
import com.nexo.repository.UserRepository;
import com.nexo.service.AchievementService;
import com.nexo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    // Constructor
    public UserController(
            UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(
            @AuthenticationPrincipal Jwt jwt)
    {

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PatchMapping("/me/username")
    public ResponseEntity<Void> changeUsername(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ChangeUsernameRequest request
            ){

        Long userId = Long.valueOf(jwt.getSubject());

        userService.changeUsername(userId, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/email")
    public ResponseEntity<Void> changeEmail(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ChangeUserEmailRequest request
            ){

        Long userId = Long.valueOf(jwt.getSubject());

        userService.changeEmail(userId, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ChangePasswordRequest request
            ){

        Long userId = Long.valueOf(jwt.getSubject());

        userService.changePassword(userId, request);

        return ResponseEntity.noContent().build();
    }
}
