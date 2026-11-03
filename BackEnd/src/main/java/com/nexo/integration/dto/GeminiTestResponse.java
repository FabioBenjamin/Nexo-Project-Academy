package com.nexo.integration.dto;

import java.util.List;

public record GeminiTestResponse(
        List<GeminiQuestionResponse> questions
) {
}
