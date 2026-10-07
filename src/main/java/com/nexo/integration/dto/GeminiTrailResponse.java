package com.nexo.nexo.integration.dto;

import java.util.List;


public record GeminiTrailResponse(
        String title,
        String description,
        List<GeminiTopicResponse> topics
) {
}
