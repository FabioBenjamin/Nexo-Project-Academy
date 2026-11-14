package com.nexo.repository;

import com.nexo.models.classes.StudyTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyTestRepository extends JpaRepository<StudyTest, Long> {

    Optional<StudyTest> findByIdAndStudyTrailUserId(Long studyTestId, Long userId);
}
