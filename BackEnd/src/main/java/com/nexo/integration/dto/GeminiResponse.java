package com.nexo.integration.dto;

public record GeminiResponse(
        GeminiTrailResponse studyTrail,
        GeminiTestResponse studyTest
) {
}
