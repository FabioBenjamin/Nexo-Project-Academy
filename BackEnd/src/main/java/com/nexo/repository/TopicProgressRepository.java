package com.nexo.repository;

import com.nexo.models.classes.TopicProgress;
import com.nexo.models.classes.TopicProgress;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicProgressRepository {

    // Seeks progress linked to a specific topic
    List<TopicProgress> findByTopicId(Long TopicId);
}
