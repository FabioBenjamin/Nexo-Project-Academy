package com.nexo.repository;

import com.nexo.models.classes.Alternative;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlternativeRepository extends JpaRepository<Alternative, Long> {

    boolean existsByIdAndQuestionId(Long alternativeId, Long questionId);

    Optional<Alternative> findByIdAndQuestionId(Long alternativeId, Long questionId);
}
