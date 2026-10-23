package com.nexo.repository;

import com.nexo.models.classes.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    // Retrieves all topics belonging to a specific study track,
    // ordered by their sequence index
    List<Topic> findByStudyTrailIdOrderByOrderIndexAsc(Long studyTraildId);

    // Search for subtopics that have a link to a main topic
    List<Topic> findByParentId(Long parentId);
}
