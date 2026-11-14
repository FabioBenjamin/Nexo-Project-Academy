package com.nexo.dto;

import java.util.List;

public record QuestionResponse(
        Long id,
        String statement,
        List<AlternativeResponse> alternatives
) {
}
