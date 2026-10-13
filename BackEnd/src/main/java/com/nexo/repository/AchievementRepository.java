package com.nexo.repository;

import com.nexo.models.classes.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {

    // // Retrieves only the achievements that a specific user has unlocked

    // NAO ALTERAR NADA QUE AINDA ESTOU ACABANDO
}
