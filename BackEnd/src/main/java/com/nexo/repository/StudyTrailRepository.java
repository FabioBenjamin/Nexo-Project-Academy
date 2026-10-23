package com.nexo.repository;

import com.nexo.dto.StudyTrailSummaryResponse;
import com.nexo.models.classes.StudyTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyTrailRepository extends JpaRepository<StudyTrail, Long> {
    // underline explicits the navigation between properties: user.id
    Optional<StudyTrail> findByIdAndUser_Id(Long studyTrailId, Long userId);

    Page<StudyTrail> findByUser_Id(Long userId, Pageable pageable);
}