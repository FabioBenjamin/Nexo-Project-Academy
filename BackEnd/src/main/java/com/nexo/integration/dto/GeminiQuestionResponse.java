package com.nexo.integration.dto;

import java.util.List;

public record GeminiQuestionResponse(
        String statement,
        List<GeminiAlternativeResponse> alternatives
) {
}
