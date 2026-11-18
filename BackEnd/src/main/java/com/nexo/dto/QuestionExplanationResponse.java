package com.nexo.dto;

public record QuestionExplanationResponse(
        Long id,
        String explanation,
        Boolean isCorrect
) {
}
