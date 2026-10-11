package com.nexo.dto;

import java.time.LocalDate;

public record StudyTrailRequestDTO(
        String content,
        LocalDate targetDate
) {
}
