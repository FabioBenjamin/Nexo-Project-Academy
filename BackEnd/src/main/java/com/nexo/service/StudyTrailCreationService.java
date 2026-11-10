package com.nexo.service;

import com.nexo.dto.StudyTrailRequestDTO;
import com.nexo.integration.dto.GeminiResponse;
import com.nexo.models.classes.StudyTrail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyTrailCreationService {

    private final StudyTestService studyTestService;
    private final StudyTrailPersistenceService studyTrailPersistenceService;

    public StudyTrailCreationService(
            StudyTestService studyTestService,
            StudyTrailPersistenceService studyTrailPersistenceService
    ){
        this.studyTestService = studyTestService;
        this.studyTrailPersistenceService = studyTrailPersistenceService;
    }

    @Transactional
    public Long persistStudyTrail(
            GeminiResponse geminiResponse,
            StudyTrailRequestDTO dto,
            Long userId)
    {

        StudyTrail studyTrail = studyTrailPersistenceService.saveStudyTrail(
                geminiResponse.studyTrail(),
                dto,
                userId);

        studyTestService.saveStudyTest(geminiResponse.studyTest(), studyTrail);

        return studyTrail.getId();
    }
}
