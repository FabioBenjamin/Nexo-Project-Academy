package com.nexo.nexo.controller;

import com.nexo.nexo.dto.LoginUserDTO;
import com.nexo.nexo.dto.RegisterUserDTO;
import com.nexo.nexo.exceptions.EmailAlreadyExistsException;
import com.nexo.nexo.exceptions.InvalidCredentialsException;
import com.nexo.nexo.exceptions.UsernameAlreadyExistsException;
import com.nexo.nexo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
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
    // and personalize HTTP response detail
    public ResponseEntity<Void> register(@RequestBody RegisterUserDTO dto){

        authService.register(dto);

        // return a HTTP response
        // Status code 201: Created
        // .build() build response entity without body
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginUserDTO dto, HttpSession session){

        Long user_id = authService.login(dto);
        // Spring automatically generates and sends session cookie
        session.setAttribute("user_id", user_id);

        // Status code 200: ok
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session){

        // Clearing session
        session.invalidate();
        return ResponseEntity.ok().build(); // Status code 200: ok
    }
}
