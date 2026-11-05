package com.devtogether.backend.tasks.dto;

import com.devtogether.backend.tasks.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(

        @NotNull(message = "Status is required")
        TaskStatus status

) {
}
