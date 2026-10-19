package com.nexo.dto;

import com.nexo.models.enums.StudyTrailStatus;

public record StudyTrailSummaryResponse(
    Long id,
    String title,
    String description,
    StudyTrailStatus status
){}
