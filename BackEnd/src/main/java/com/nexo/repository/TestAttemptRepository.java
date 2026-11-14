package com.nexo.repository;

import com.nexo.models.classes.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
}
