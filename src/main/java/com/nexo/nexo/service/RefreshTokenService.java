package com.nexo.nexo.service;

import com.nexo.nexo.exceptions.InvalidRefreshTokenException;
import com.nexo.nexo.models.classes.RefreshToken;
import com.nexo.nexo.models.classes.User;
import com.nexo.nexo.repository.RefreshTokenRepository;
import com.nexo.nexo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken generateRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();

        // Generates a random sequence and convert to String
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpires_at(LocalDateTime.now().plusDays(30));

        // Save in db and return the Obj
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token){

        // Get the refresh token
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        // Verify if it's not expired
        if(refreshToken.getExpires_at().isBefore(LocalDateTime.now())){
            throw new InvalidRefreshTokenException("Refresh Token expired");
        }

        return refreshToken;
    }
}
