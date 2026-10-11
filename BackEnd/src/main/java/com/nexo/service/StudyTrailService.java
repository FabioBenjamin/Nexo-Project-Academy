package com.nexo.service;

import com.nexo.dto.StudyTrailRequestDTO;
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

    // constructor
    public StudyTrailService(
            GeminiClient geminiClient,
            StudyTrailPersistenceService studyTrailPersistenceService){

        this.geminiClient = geminiClient;
        this.studyTrailPersistenceService = studyTrailPersistenceService;
    }

    public Long createStudyTrail(StudyTrailRequestDTO dto, Long user_id){

        // Gemini response converted into Java Class
        GeminiTrailResponse trailResponse = geminiClient.generateTrail(dto, LocalDate.now());

        return studyTrailPersistenceService.saveStudyTrail(trailResponse, dto, user_id).getId();
    }
}
