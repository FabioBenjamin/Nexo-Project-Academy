package com.nexo.service;

import com.nexo.models.classes.Topic;
import com.nexo.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicService {

    // final creates constant var. So there will be only one topicRepository instance
    // that will be created once and cannot be modified
    // dependencies
    private final TopicRepository topicRepository;
    private final TopicProgressService topicProgressService;

    // Construtor
    public TopicService(TopicRepository topicRepository, TopicProgressService topicProgressService) {
        this.topicRepository = topicRepository;
        this.topicProgressService = topicProgressService;
    }

    // Returns the list of topics belonging
    // To a track, ordered by index
    @Transactional(readOnly = true)
    public List<Topic> getTopicsByTrailId(Long trailId) {
        return topicRepository.findByStudyTrailIdOrderByOrderIndexAsc(trailId);
    }

    // Conclusion of the topic and update a gamification
    @Transactional
    public void completeTopic(Long userId, Long topicId) {
        topicProgressService.completeTopic(userId, topicId);
    }
}