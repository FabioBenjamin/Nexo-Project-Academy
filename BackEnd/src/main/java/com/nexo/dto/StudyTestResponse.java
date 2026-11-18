package com.nexo.dto;

import java.util.List;

public record StudyTestResponse(
        List<QuestionResponse> questions
) {
}
