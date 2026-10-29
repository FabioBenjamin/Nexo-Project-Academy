package com.nexo.service;

import com.nexo.models.classes.TopicProgress;
import com.nexo.models.enums.TopicProgressStatus;
import com.nexo.repository.TopicProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TopicProgressService {

    // Dependence
    private final TopicProgressRepository topicProgressRepository;
    private final AchievementService achievementService;

    // Construtor
    public TopicProgressService(TopicProgressRepository topicProgressRepository, AchievementService achievementService) {

        this.topicProgressRepository = topicProgressRepository;
        this.achievementService = achievementService;
    }

    // Marks the topics as completed
    // Notifies the gamification system
    @Transactional
    public void completeTopic(Long userId, Long topicId) {
        TopicProgress progress = topicProgressRepository.findByTopic_Id(topicId)
                .orElseThrow(() -> new RuntimeException("TopicProgress not found with id " + topicId));

        progress.setStatus(TopicProgressStatus.COMPLETED);
        progress.setCompletedAt(LocalDate.now());

        // Save changes
        topicProgressRepository.save(progress);

        // Handles offense processing
        // Activity windows and nighttime hours (after -> 22:00)
        achievementService.processActivityCompletion(userId);
    }
}