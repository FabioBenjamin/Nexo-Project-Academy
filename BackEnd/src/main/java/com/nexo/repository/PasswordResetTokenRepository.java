package com.nexo.repository;

import com.nexo.models.classes.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findTopByUser_EmailAndCodeAndUsedFalseOrderByExpiresAtDesc(String email, String code);
}