package com.nexo.service;

import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.dto.StudyTrailResponse;
import com.nexo.integration.dto.GeminiTopicResponse;
import com.nexo.integration.dto.GeminiTrailResponse;
import com.nexo.integration.gemini.GeminiClient;
import com.nexo.models.classes.StudyTrail;
import com.nexo.models.classes.User;
import com.nexo.models.enums.StudyTrailStatus;
import com.nexo.repository.StudyTrailRepository;
import com.nexo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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

    public StudyTrailResponse getStudyTrail(Long studyTrailId, Long userId){

        StudyTrail studyTrail = studyTrailRepository.findById(studyTrailId)
                .orElseThrow(() -> new RuntimeException("Study trail not found"));

    }
}
