package com.nexo.repository;

import com.nexo.models.classes.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA understand the methods' name and generate query automatically
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    // Persists the relationship between User and Achievement in the join table
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO userachievement (user_id, achievement_id, achievement_date) " +
            "VALUES (:userId, :achievementId, CURRENT_DATE) ON CONFLICT DO NOTHING", nativeQuery = true)
    void insertUserAchievement(@Param("userId") Long userId, @Param("achievementId") Long achievementId);

}