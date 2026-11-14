package com.nexo.dto;

import java.util.List;

public record TestAttemptResultRequest(

        List<AnswerRequest> answers
) {
}
