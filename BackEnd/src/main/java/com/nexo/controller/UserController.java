package com.nexo.controller;

import com.nexo.dto.AchievementDTO;
import com.nexo.dto.UserProfileResponseDTO;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.models.classes.User;
import com.nexo.repository.UserRepository;
import com.nexo.service.AchievementService;
import com.nexo.service.UserService;
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

    private final UserService userService;

    // Contrutor
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
}
