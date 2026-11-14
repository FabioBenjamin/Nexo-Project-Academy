package com.nexo.repository;

import com.nexo.models.classes.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("""
    SELECT COUNT(q)
    FROM Question q
    WHERE q.studyTest.id = :studyTestId
    AND q.id IN :questionIds
""")
    long countQuestionsBelongingToStudyTest(
            Long studyTestId,
            List<Long> questionIds
    );
}
