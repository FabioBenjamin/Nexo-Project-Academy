package com.nexo.dto;

import com.nexo.models.enums.TopicProgressStatus;

import java.time.LocalDate;
import java.util.List;

public record TopicResponse(
        Long id,
        String title,
        String description,
        LocalDate targetDate,
        TopicProgressStatus status,
        LocalDate completedAt,
        List<TopicResponse> subTopics
) {
}
