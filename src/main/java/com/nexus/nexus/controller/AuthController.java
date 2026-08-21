package com.nexus.nexus.controller;

import com.nexus.nexus.dto.RegisterUserDTO;
import com.nexus.nexus.exceptions.EmailAlreadyExistsException;
import com.nexus.nexus.exceptions.UsernameAlreadyExistsException;
import com.nexus.nexus.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// Like a prefix, all routes will start with /auth
@RequestMapping("/auth")
public class AuthController {

    // dependencies
    private final AuthService authService;

    // Constructor
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    // Use response entity because it allows you to control
    // and personalize HTTP response details
    public ResponseEntity<String> register(@RequestBody RegisterUserDTO dto){

        try {
            authService.register(dto);

            // return a HTTP response
            return ResponseEntity
                    .status(201) // 201: created
                    .body("User registered successfully");

        } catch (EmailAlreadyExistsException e) {
            // return a HTTP response
            return ResponseEntity
                    .status(409) // 409: Conflict
                    .body(e.getMessage());

        } catch (UsernameAlreadyExistsException e){
            // return HHTP response
            return ResponseEntity
                    .status(409)
                    .body(e.getMessage());
        }
    }
}
