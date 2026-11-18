package com.nexo.dto;

import com.nexo.models.enums.StudyTrailStatus;

import java.time.LocalDate;
import java.util.List;

public record StudyTrailResponse(
        Long id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate targetDate,
        StudyTrailStatus status,
        List<TopicResponse> topics
) {
}
