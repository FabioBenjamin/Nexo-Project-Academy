package com.nexo.service;

import com.nexo.dto.*;
import com.nexo.exceptions.InvalidPageNumberException;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.integration.dto.GeminiTrailResponse;
import com.nexo.integration.gemini.GeminiClient;
import com.nexo.models.classes.StudyTrail;
import com.nexo.models.classes.Topic;
import com.nexo.models.classes.TopicProgress;
import com.nexo.repository.StudyTrailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudyTrailService {

    // dependencies
    private final GeminiClient geminiClient;
    private final StudyTrailPersistenceService studyTrailPersistenceService;
    private final StudyTrailRepository studyTrailRepository;

    // constructor
    public StudyTrailService(
            GeminiClient geminiClient,
            StudyTrailPersistenceService studyTrailPersistenceService,
            StudyTrailRepository studyTrailRepository){

        this.geminiClient = geminiClient;
        this.studyTrailPersistenceService = studyTrailPersistenceService;
        this.studyTrailRepository = studyTrailRepository;
    }

    public Long createStudyTrail(StudyTrailRequestDTO dto, Long userId){

        // Gemini response converted into Java Class
        GeminiTrailResponse trailResponse = geminiClient.generateTrail(dto, LocalDate.now());

        return studyTrailPersistenceService.saveStudyTrail(trailResponse, dto, userId).getId();
    }

    @Transactional(readOnly = true)
    public StudyTrailResponse getStudyTrail(Long studyTrailId, Long userId){

        StudyTrail studyTrail = studyTrailRepository.findByIdAndUser_Id(studyTrailId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Study trail not found"));

        List<TopicResponse> topics = studyTrail.getTopics()
                .stream() // iterates over each topic into list
                // filter the elements, which satisfies a specific condition, that will continue in stream operation
                .filter(topic -> topic.getParentTopic() == null)
                .map(this::toTopicResponse) // apply this function to each element
                .toList(); // gather elements into a list

        return new StudyTrailResponse(
                studyTrailId,
                studyTrail.getTitle(),
                studyTrail.getDescription(),
                studyTrail.getStartDate(),
                studyTrail.getTargetDate(),
                studyTrail.getStatus(),
                topics
        );
    }

    private TopicResponse toTopicResponse(Topic topic){

        List<TopicResponse> subTopics = topic.getSubTopics()
                .stream()
                .map(this::toTopicResponse)
                .toList();

        TopicProgress progress = topic.getProgress();

        return new TopicResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getDescription(),
                progress.getTargetDate(),
                progress.getStatus(),
                progress.getCompletedAt(),
                subTopics
        );
    }

    public Page<StudyTrailSummaryResponse> getUserTrails(Long userId, int page, int size){

        if(page < 0){
            throw new InvalidPageNumberException("Page number can't be negative");
        }

        Pageable pageable = PageRequest.of(page, size);

        return studyTrailRepository.findByUser_Id(userId, pageable)
                .map(trail -> new StudyTrailSummaryResponse(
                        trail.getId(),
                        trail.getTitle(),
                        trail.getDescription(),
                        trail.getStatus()
                ));
    }

    @Transactional
    public void deleteStudyTrail(Long studyTrailId, Long userId){

        StudyTrail studyTrail = studyTrailRepository.findByIdAndUser_Id(studyTrailId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Study trail not found"));

        studyTrailRepository.delete(studyTrail);
    }

    @Transactional
    public void updateStudyTrail(Long studyTrailId, Long userId, UpdateStudyTrailRequest request){

        StudyTrail studyTrail = studyTrailRepository.findByIdAndUser_Id(studyTrailId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Study trail not found"));

        if(request.getTitle() != null){
            // Validate blank titles like: "    ", ""
            if(request.getTitle().isBlank()){
                throw new IllegalArgumentException("Title can't be null");
            }
            // assign new value
            studyTrail.setTitle(request.getTitle());
        }

        if(request.getDescription() != null){
            // Validate blank descriptions like: "    ", ""
            if(request.getDescription().isBlank()){
                throw new IllegalArgumentException("Description can't be null");
            }
            // assign new value
            studyTrail.setDescription(request.getDescription());
        }

        studyTrailRepository.save(studyTrail);
    }
}
