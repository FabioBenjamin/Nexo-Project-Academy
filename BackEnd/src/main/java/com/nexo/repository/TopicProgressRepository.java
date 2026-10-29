package com.nexo.repository;

import com.nexo.models.classes.TopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicProgressRepository extends JpaRepository<TopicProgress, Long> {

    // Seeks progress linked to a specific topic
    Optional<TopicProgress> findByTopic_Id(Long topicId);

}
