package com.nexo.repository;

import com.nexo.models.classes.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // Retrieves only the achievements that a specific user has unlocked
    @Query(value = "SELECT a.* FROM achievement a " +
            "JOIN userachievement ua ON a.achievement_id = ua.achievement_id " +
            "WHERE ua.user_id = :userId", nativeQuery = true)
    List<Achievement> findUnlockedByUserId(@Param("userId") Long userId);

    // Checks if the user has a specific achievement (prevents duplicates)
    @Query(value = "SELECT EXISTS(SELECT 1 FROM userachievement " +
            "WHERE user_id = :userId AND achievement_id = :achievementId)", nativeQuery = true)
    boolean existsByUserIdAndAchievementId(@Param("userId") Long userId, @Param("achievementId") Long achievementId);
}
