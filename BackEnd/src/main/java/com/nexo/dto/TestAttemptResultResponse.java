package com.nexo.dto;

import java.util.List;

public record TestAttemptResultResponse(
        List<QuestionExplanationResponse> questionExplanations,
        int score,
        boolean approved
){
}
