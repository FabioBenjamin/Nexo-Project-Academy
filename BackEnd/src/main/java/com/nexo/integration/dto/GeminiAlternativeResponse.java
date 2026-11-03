package com.nexo.integration.dto;

public record GeminiAlternativeResponse(
        String description,
        Boolean isCorrect,
        String explanation
) {
}
