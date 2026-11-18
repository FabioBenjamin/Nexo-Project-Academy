package com.nexo.repository;

import com.nexo.models.classes.AchievementRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRequirementRepository extends JpaRepository<AchievementRequirement, Long> {
    List<AchievementRequirement> findByAchievementId(Long achievementId);
}