package com.nexo.service;

import com.nexo.dto.*;
import com.nexo.models.classes.RefreshToken;
import com.nexo.security.JwtService;
import com.nexo.exceptions.EmailAlreadyExistsException;
import com.nexo.exceptions.InvalidCredentialsException;
import com.nexo.exceptions.InvalidEmailException;
import com.nexo.exceptions.UsernameAlreadyExistsException;
import com.nexo.models.classes.User;
import com.nexo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.EmailValidator;

@Service
public class AuthService {

    // final creates constant var. So there will be only one userRepository instance
    // that will be created once and cannot be modified
    // dependencies
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, JwtService jwtService, RefreshTokenService refreshTokenService){
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.emailValidator = EmailValidator.getInstance();
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(RegisterUserDTO dto){

        // valid username
        if (userRepository.existsByUsername(dto.getUsername())){
            throw new UsernameAlreadyExistsException("Username is taken. Please choose another one");
        }
        // valid email
        if(!emailValidator.isValid(dto.getEmail())){
            throw new InvalidEmailException("Invalid Email!");
        }
        // email in use
        if (userRepository.existsByEmail(dto.getEmail())){
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        // Hash password
        String hash_password = passwordEncoder.encode(dto.getPassword());

        User new_user = new User();

        new_user.setEmail(dto.getEmail());
        new_user.setUsername(dto.getUsername());
        new_user.setPassword(hash_password);

        userRepository.save(new_user);
    }

    public LoginResponseDTO login(LoginUserDTO dto){

        // find user by username if exists, else throw exception
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        // Verify password
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        String accessToken = jwtService.generateToken(user.getId());

        String refreshToken = refreshTokenService
                .generateRefreshToken(user)
                .getToken();

        return new LoginResponseDTO(
                accessToken,
                refreshToken
        );
    }

    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest dto){

        RefreshToken refreshToken = refreshTokenService
                .validateRefreshToken(dto.refreshToken());

        User user = refreshToken.getUser();

        String accessToken = jwtService
                .generateToken(user.getId());

        return new AccessTokenResponse(accessToken);
    }

    public void logout(RefreshTokenRequest dto){
        refreshTokenService.revokeRefreshToken(dto.refreshToken());
    }
}
