package com.nexo.service;

import com.nexo.models.enums.StudyTrailStatus;
import com.nexo.dto.UpdateTopicProgressRequest;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.models.classes.StudyTrail;
import com.nexo.models.classes.Topic;
import com.nexo.models.enums.StudyTestStatus;
import com.nexo.models.enums.TopicProgressStatus;
import com.nexo.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TopicProgressService {

    // Dependencies
    private final AchievementService achievementService;
    private final TopicRepository topicRepository;

    // Constructor
    public TopicProgressService(
            AchievementService achievementService,
            TopicRepository topicRepository) {

        this.achievementService = achievementService;
        this.topicRepository = topicRepository;
    }

    // Marks the topics as completed
    // Notifies the gamification system
    @Transactional
    public void updateTopicProgress(Long userId, Long topicId, UpdateTopicProgressRequest request) {

        Topic topic = topicRepository.findByIdAndStudyTrailUserId(topicId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        // valid null values
        if (request.completed() == null){
            throw new IllegalArgumentException("Value must not be null.");
        }

        boolean completed = request.completed();

        // If user complete a topic
        if(completed){
            // verify if is a parent topic
            if (topic.getParentTopic() == null){
                completeTopic(topic, userId);
            }
            else{
                completeSubTopic(topic, userId);
            }
        } else {  // It means that user unmark a complete topic
            if(topic.getParentTopic() == null){
                reOpenTopic(topic);
            }
            else{
                reOpenSubTopic(topic);
            }
        }
        // there is no need to call repository.save(). Hibernate manage the entities
        // automatically and Dirty Checking persist the changes inside a transactional method
    }

    // complete a main Topic
    private void completeTopic(Topic topic, Long userId){

        if(topic.getProgress().getStatus() == TopicProgressStatus.COMPLETED){
            return;
        }

        // Update the status of parent topic
        topic.getProgress().setStatus(TopicProgressStatus.COMPLETED);
        topic.getProgress().setCompletedAt(LocalDate.now());

        // Update all subTopics
        for(Topic subTopic : topic.getSubTopics()){
            subTopic.getProgress().setStatus(TopicProgressStatus.COMPLETED);
            subTopic.getProgress().setCompletedAt(LocalDate.now());
        }

        // Assign xp for complete a topic
        processCompletionXp(topic, userId);

        // Verify if the completed topic was the remaining one
        if(shouldOpenStudyTest(topic.getStudyTrail())){
            // open studyTest
            topic.getStudyTrail().setStatus(StudyTrailStatus.COMPLETED);
            topic.getStudyTrail().getStudyTest().setStatus(StudyTestStatus.OPEN);
            achievementService.processActivityCompletion(userId);
        }
    }

    private void completeSubTopic(Topic subTopic, Long userId){

        if(subTopic.getProgress().getStatus() == TopicProgressStatus.COMPLETED){
            return;
        }

        subTopic.getProgress().setStatus(TopicProgressStatus.COMPLETED);
        subTopic.getProgress().setCompletedAt(LocalDate.now());

        Topic parentTopic = subTopic.getParentTopic();

        // Verify if the completed topic is the last pending one
        boolean allCompleted = parentTopic.getSubTopics()
                .stream()
                .allMatch(topic ->
                        topic.getProgress().getStatus() == TopicProgressStatus.COMPLETED
                );


        if(allCompleted){
            // Complete Parent topic
            parentTopic.getProgress().setCompletedAt(LocalDate.now());
            parentTopic.getProgress().setStatus(TopicProgressStatus.COMPLETED);

            // Assign xp for complete a topic
            processCompletionXp(parentTopic, userId);

            // Verify if the completed topic was the remaining one
            if(shouldOpenStudyTest(parentTopic.getStudyTrail())){
                // open studyTest
                parentTopic.getStudyTrail().getStudyTest().setStatus(StudyTestStatus.OPEN);
                achievementService.processActivityCompletion(userId);
            }
        }
    }

    private void reOpenTopic(Topic topic){

        // ReOpen parent/main Topic
        topic.getProgress().setCompletedAt(null);
        topic.getProgress().setStatus(TopicProgressStatus.IN_PROGRESS);

        // ReOpen subTopics
        for(Topic subTopic : topic.getSubTopics()){
            subTopic.getProgress().setCompletedAt(null);
            subTopic.getProgress().setStatus(TopicProgressStatus.IN_PROGRESS);
        }
    }

    private void reOpenSubTopic(Topic subTopic){

        // ReOpen subTopic
        subTopic.getProgress().setStatus(TopicProgressStatus.IN_PROGRESS);
        subTopic.getProgress().setCompletedAt(null);

        // ReOpen parent Topic
        Topic parentTopic = subTopic.getParentTopic();
        parentTopic.getProgress().setCompletedAt(null);
        parentTopic.getProgress().setStatus(TopicProgressStatus.IN_PROGRESS);
    }

    private void processCompletionXp(Topic topic, Long userId){
        // If user has already received Xp for complete this topic
        if(topic.getProgress().isXpAwarded() == null || topic.getProgress().isXpAwarded()){
            return;
        }

        topic.getProgress().setXpAwarded(true);
        achievementService.processActivityCompletion(userId);
    }

    private boolean shouldOpenStudyTest(StudyTrail studyTrail){

        if(studyTrail.getStudyTest().getStatus() != StudyTestStatus.BLOCKED) {
            return false;
        }

        // Verify if all topics are completed
        return studyTrail.getTopics()
                .stream()
                .allMatch(topic ->
                        topic.getProgress().getStatus() == TopicProgressStatus.COMPLETED);
    }
}