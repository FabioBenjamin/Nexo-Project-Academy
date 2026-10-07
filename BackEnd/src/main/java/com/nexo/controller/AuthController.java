package com.nexo.controller;

import com.nexo.dto.*;
import com.nexo.integration.gemini.GeminiClient;
import com.nexo.security.JwtService;
import com.nexo.service.AuthService;
import com.nexo.service.RefreshTokenService;
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
    private final JwtService jwtService;

    // Constructor
    public AuthController(AuthService authService, JwtService jwtService){
        this.authService = authService;
        this.jwtService = jwtService;
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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginUserDTO dto){

        // Status code 200: ok
        // return JWT token
        return ResponseEntity.ok(authService.login(dto));
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest dto){

        authService.logout(dto);
        // No content -> Status Code 204
        // it means: the operation was successful and there is no content to return
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@RequestBody RefreshTokenRequest dto){
        return ResponseEntity.ok(
                authService.refreshAccessToken(dto)
        );
    }

    @GetMapping("/jwt/test")
    public ResponseEntity<String> getJwt(){
        long num = 1;
        String token = jwtService.generateToken(num);

        return ResponseEntity.ok(token);
    }

}
