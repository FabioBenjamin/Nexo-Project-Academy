package com.nexo.service;

import com.nexo.dto.*;
import com.nexo.exceptions.InvalidPageNumberException;
import com.nexo.exceptions.EntityNotFoundException;
import com.nexo.integration.dto.GeminiResponse;
import com.nexo.integration.gemini.GeminiClient;
import com.nexo.models.classes.*;
import com.nexo.models.enums.StudyTrailStatus;
import com.nexo.repository.StudyTrailRepository;
import com.nexo.repository.SettingsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudyTrailService {

    private final GeminiClient geminiClient;
    private final StudyTrailCreationService studyTrailCreationService;
    private final StudyTrailRepository studyTrailRepository;
    private final AchievementService achievementService;
    private final SettingsRepository settingsRepository;

    public StudyTrailService(
            GeminiClient geminiClient,
            StudyTrailRepository studyTrailRepository,
            StudyTrailCreationService studyTrailCreationService,
            AchievementService achievementService,
            SettingsRepository settingsRepository) {

        this.geminiClient = geminiClient;
        this.studyTrailRepository = studyTrailRepository;
        this.studyTrailCreationService = studyTrailCreationService;
        this.achievementService = achievementService;
        this.settingsRepository = settingsRepository;
    }

    public Long createStudyTrail(StudyTrailRequestDTO dto, Long userId) {
        UserSettings settings = settingsRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("User settings not found"));

        GeminiResponse geminiResponse = geminiClient.generateTrail(
                dto,
                LocalDate.now(),
                settings.getLearningProfile()
        );

        return studyTrailCreationService.persistStudyTrail(geminiResponse, dto, userId);
    }

    // ... restante dos métodos do StudyTrailService permanecem idênticos ...
}