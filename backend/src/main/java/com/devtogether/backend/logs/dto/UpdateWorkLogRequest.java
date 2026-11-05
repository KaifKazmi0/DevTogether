package com.devtogether.backend.logs.dto;

import com.devtogether.backend.logs.entity.WorkLogCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateWorkLogRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Category is required")
        WorkLogCategory category,

        @NotNull(message = "Log date is required")
        LocalDate logDate,

        @NotNull(message = "Hours spent is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Hours spent must be greater than zero")
        @DecimalMax(value = "24.00", inclusive = true, message = "Hours spent must not exceed 24 hours")
        BigDecimal hoursSpent

) {
}
