package com.nexus.nexus.controller;

import com.nexus.nexus.dto.LoginUserDTO;
import com.nexus.nexus.dto.RegisterUserDTO;
import com.nexus.nexus.exceptions.EmailAlreadyExistsException;
import com.nexus.nexus.exceptions.InvalidCredentialsException;
import com.nexus.nexus.exceptions.UsernameAlreadyExistsException;
import com.nexus.nexus.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    .status(201) // Status code 201: created
                    .body("User registered successfully");

        } catch (EmailAlreadyExistsException e) {
            // return a HTTP response
            return ResponseEntity
                    .status(409) // Status code 409: Conflict
                    .body(e.getMessage());

        } catch (UsernameAlreadyExistsException e){
            // return HHTP response
            return ResponseEntity
                    .status(409) // Status code 409: Conflict
                    .body(e.getMessage());
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserDTO dto, HttpSession session){

        try{
            Long user_id = authService.login(dto);
            // Spring automatically generates and sends session cookie
            session.setAttribute("user_id", user_id);

            // Status code 200: ok
            return ResponseEntity.status(200).body("Login successfully");

        } catch (InvalidCredentialsException e){
            // Return HTTP response
            return ResponseEntity
                    .status(409) // Status code 409: Conflict
                    .body(e.getMessage());
        }
    }
}
