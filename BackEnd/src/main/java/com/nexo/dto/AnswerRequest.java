package com.nexo.dto;

public record AnswerRequest(

        Long questionId,
        Long alternativeId
) {
}
