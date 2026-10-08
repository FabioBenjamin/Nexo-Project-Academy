package com.nexo.nexo.integration.dto;


import java.time.LocalDate;
import java.util.List;

public record GeminiTopicResponse(
        String title,
        String description,
        List<GeminiTopicResponse> subTopics,
        LocalDate targetDate
) {
}
