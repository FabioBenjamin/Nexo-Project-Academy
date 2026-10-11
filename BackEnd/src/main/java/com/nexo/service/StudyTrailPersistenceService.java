package com.nexo.service;

import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.integration.dto.GeminiTopicResponse;
import com.nexo.integration.dto.GeminiTrailResponse;
import com.nexo.models.classes.StudyTrail;
import com.nexo.models.classes.TopicProgress;
import com.nexo.models.classes.User;
import com.nexo.models.classes.Topic;
import com.nexo.models.enums.StudyTrailStatus;
import com.nexo.models.enums.TopicProgressStatus;
import com.nexo.repository.StudyTrailRepository;
import com.nexo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StudyTrailPersistenceService {

    private final UserRepository userRepository;
    private final StudyTrailRepository studyTrailRepository;

    public StudyTrailPersistenceService(
            UserRepository userRepository,
            StudyTrailRepository studyTrailRepository){

        this.userRepository = userRepository;
        this.studyTrailRepository = studyTrailRepository;
    }

    @Transactional
    public StudyTrail saveStudyTrail(
            GeminiTrailResponse trailResponse,
            StudyTrailRequestDTO dto,
            Long user_id){

        // find user
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // create StudyTrail Object and set attributes
        StudyTrail studyTrail = new StudyTrail();
        studyTrail.setTitle(trailResponse.title());
        studyTrail.setDescription(trailResponse.description());
        studyTrail.setTargetDate(dto.targetDate());
        studyTrail.setStatus(StudyTrailStatus.IN_PROGRESS);
        studyTrail.setUser(user);

        // iterate over topics array
        for(int i = 0; i < trailResponse.topics().size(); i++){
            // Current topic of list
            GeminiTopicResponse topicResponse = trailResponse.topics().get(i);

            // create Topic and set attributes
            Topic topic = new Topic();
            topic.setTitle(topicResponse.title());
            topic.setDescription(topicResponse.description());
            topic.setOrderIndex(i + 1);

            // Create TopicProgress and set attributes
            TopicProgress progress = new TopicProgress();
            progress.setTargetDate(topicResponse.targetDate());
            progress.setStatus(TopicProgressStatus.IN_PROGRESS);

            // Save Topic progress
            topic.setProgress(progress);

            if(topicResponse.subTopics() == null){
                topic.setStudyTrail(studyTrail);
                studyTrail.getTopics().add(topic);
                continue;
            }

            // iterate over each subtopic(list) in Current Topic
            for(int x = 0; x < topicResponse.subTopics().size(); x++){
                GeminiTopicResponse subTopicResponse = topicResponse.subTopics().get(x);

                // create subTopic and set attributes
                Topic subTopic = new Topic();
                subTopic.setTitle(subTopicResponse.title());
                subTopic.setDescription(subTopicResponse.description());
                subTopic.setOrderIndex(x + 1);
                subTopic.setParentTopic(topic);

                // Create TopicProgress and set attributes
                TopicProgress subTopicProgress = new TopicProgress();
                subTopicProgress.setTargetDate(subTopicResponse.targetDate());
                subTopicProgress.setStatus(TopicProgressStatus.IN_PROGRESS);

                // Save Topic progress
                subTopic.setProgress(subTopicProgress);

                // Add to list
                topic.getSubTopics().add(subTopic);

                // Set SubTopic trail
                subTopic.setStudyTrail(studyTrail);

            }

            // set Topic trail
            topic.setStudyTrail(studyTrail);
            studyTrail.getTopics().add(topic);  // Add topic into Trail topics list

        }

        return studyTrailRepository.save(studyTrail);
    }

}
