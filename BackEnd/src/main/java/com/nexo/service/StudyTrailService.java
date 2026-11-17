package com.nexo.service;

import com.nexo.dto.*;
import com.nexo.exceptions.InvalidPageNumberException;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.integration.dto.GeminiResponse;
import com.nexo.integration.gemini.GeminiClient;
import com.nexo.models.classes.*;
import com.nexo.models.enums.StudyTrailStatus;
import com.nexo.repository.StudyTrailRepository;
import com.nexo.repository.UserRepository;
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
    private final StudyTrailCreationService studyTrailCreationService;
    private final StudyTrailRepository studyTrailRepository;
    private final AchievementService achievementService;
    private final UserRepository userRepository;

    // constructor
    public StudyTrailService(
            GeminiClient geminiClient,
            StudyTrailRepository studyTrailRepository,
            StudyTrailCreationService studyTrailCreationService,
            AchievementService achievementService,
            UserRepository userRepository) {

        this.geminiClient = geminiClient;
        this.studyTrailRepository = studyTrailRepository;
        this.studyTrailCreationService = studyTrailCreationService;
        this.achievementService = achievementService;
        this.userRepository = userRepository;
    }

    public Long createStudyTrail(StudyTrailRequestDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Gemini response converted into Java Class com as regras permanentes do perfil do usuário
        GeminiResponse geminiResponse = geminiClient.generateTrail(
                dto,
                LocalDate.now(),
                user.getLearningProfile()
        );

        return studyTrailCreationService.persistStudyTrail(geminiResponse, dto, userId);
    }

    @Transactional(readOnly = true)
    public StudyTrailResponse getStudyTrail(Long studyTrailId, Long userId) {

        StudyTrail studyTrail = studyTrailRepository.findByIdAndUser_Id(studyTrailId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Study trail not found"));

        List<TopicResponse> topics = studyTrail.getTopics()
                .stream()
                .filter(topic -> topic.getParentTopic() == null)
                .map(this::toTopicResponse)
                .toList();

        StudyTest studyTest = studyTrail.getStudyTest();

        return new StudyTrailResponse(
                studyTrailId,
                studyTrail.getTitle(),
                studyTrail.getDescription(),
                studyTrail.getStartDate(),
                studyTrail.getTargetDate(),
                studyTrail.getStatus(),
                topics,
                mapToStudyTestPreviewDTO(studyTest)
        );
    }

    private StudyTestPreviewResponse mapToStudyTestPreviewDTO(StudyTest studyTest) {

        if (studyTest == null) {
            return null;
        }

        List<TestAttempt> testAttempts = studyTest.getTestAttempts();
        Integer userScore = null;

        if (testAttempts != null && !testAttempts.isEmpty()) {
            userScore = testAttempts.getLast().getScore();
        }

        return new StudyTestPreviewResponse(
                studyTest.getId(),
                studyTest.getStatus(),
                studyTest.getPassingScore(),
                userScore
        );
    }

    private TopicResponse toTopicResponse(Topic topic) {

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

    public Page<StudyTrailSummaryResponse> getUserTrails(Long userId, int page, int size) {

        if (page < 0) {
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
    public void deleteStudyTrail(Long studyTrailId, Long userId) {

        StudyTrail studyTrail = studyTrailRepository.findByIdAndUser_Id(studyTrailId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Study trail not found"));

        studyTrailRepository.delete(studyTrail);
    }

    @Transactional
    public void updateStudyTrail(Long studyTrailId, Long userId, UpdateStudyTrailRequest request) {

        StudyTrail studyTrail = studyTrailRepository.findByIdAndUser_Id(studyTrailId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Study trail not found"));

        if (request.getTitle() == null && request.getDescription() == null) {
            throw new IllegalArgumentException("Must provide a value to update information");
        }

        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) {
                throw new IllegalArgumentException("Title can't be null");
            }
            studyTrail.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            if (request.getDescription().isBlank()) {
                throw new IllegalArgumentException("Description can't be null");
            }
            studyTrail.setDescription(request.getDescription());
        }

        studyTrailRepository.save(studyTrail);
    }

    public void completeStudyTrail(StudyTrail studyTrail) {

        studyTrail.setStatus(StudyTrailStatus.COMPLETED);

        Long userId = studyTrail.getUser().getId();

        achievementService.processTrailCompletion(userId);
        achievementService.addXpCheckAchievement(userId, 300);

    }
}