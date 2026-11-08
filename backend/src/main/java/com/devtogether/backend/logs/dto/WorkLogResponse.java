package com.devtogether.backend.logs.dto;

import com.devtogether.backend.logs.entity.WorkLogCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkLogResponse(
        Long id,
        String title,
        String description,
        WorkLogCategory category,
        LocalDate logDate,
        BigDecimal hoursSpent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
