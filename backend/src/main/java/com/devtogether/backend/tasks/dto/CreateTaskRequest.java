package com.devtogether.backend.tasks.dto;

import com.devtogether.backend.tasks.entity.TaskPriority;
import com.devtogether.backend.tasks.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTaskRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "User ID is required")
        Long userId,

        TaskStatus status,

        TaskPriority priority,

        LocalDate dueDate

) {
}
