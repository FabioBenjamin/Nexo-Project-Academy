package com.nexo.repository;

import com.nexo.models.classes.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {

    Optional<Settings> findByUserId(Long userId);


}
