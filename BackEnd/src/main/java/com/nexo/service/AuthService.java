package com.nexo.service;

import com.nexo.dto.*;
import com.nexo.exceptions.EmailAlreadyExistsException;
import com.nexo.exceptions.InvalidCredentialsException;
import com.nexo.exceptions.InvalidEmailException;
import com.nexo.exceptions.UsernameAlreadyExistsException;
import com.nexo.models.classes.PasswordResetToken;
import com.nexo.models.classes.RefreshToken;
import com.nexo.models.classes.User;
import com.nexo.repository.PasswordResetTokenRepository;
import com.nexo.repository.UserRepository;
import com.nexo.security.JwtService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService) 
            {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = EmailValidator.getInstance();
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
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

        // find user by username or email, if exists, else throw exception
        User user = userRepository.findUserInLogin(dto.getUser())
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
                refreshToken,
                user.getUsername(),
                user.getEmail()
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

    public void requestPasswordReset(ForgotPasswordRequest dto) {
        if (dto.email() == null || dto.email().isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }

        userRepository.findByEmail(dto.email().trim()).ifPresent(user -> {
            String code = String.format("%06d", new java.security.SecureRandom().nextInt(1_000_000));

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setCode(code);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            resetToken.setUsed(false);

            passwordResetTokenRepository.save(resetToken);

            // Print to the console for local testing
            System.out.println("[NEXO RECOVERY] Código de recuperação para " + user.getEmail() + ": " + code);

            try {
                emailService.sendPasswordResetCode(user.getEmail(), user.getUsername(), code);
            } catch (Exception e) {
                System.err.println("[NEXO RECOVERY] Falha ao enviar e-mail SMTP (verifique o .env): " + e.getMessage());
            }
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest dto) {
        if (dto.email() == null || dto.code() == null || dto.newPassword() == null) {
            throw new IllegalArgumentException("All fields are required");
        }
        if (dto.newPassword().length() < 6) {
            throw new IllegalArgumentException("Password must have at least 6 characters");
        }
        if (!dto.newPassword().equals(dto.confirmNewPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findTopByUser_EmailAndCodeAndUsedFalseOrderByExpiresAtDesc(dto.email().trim(), dto.code().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired recovery code"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Recovery code has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
