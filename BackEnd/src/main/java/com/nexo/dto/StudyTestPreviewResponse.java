package com.nexo.dto;

import com.nexo.models.enums.StudyTestStatus;

public record StudyTestPreviewResponse(
        Long id,
        StudyTestStatus studyTestStatus,
        int passingScore,
        Integer userScore
) {
}
