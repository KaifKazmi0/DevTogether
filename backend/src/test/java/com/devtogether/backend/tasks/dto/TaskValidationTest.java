package com.devtogether.backend.tasks.dto;

import com.devtogether.backend.tasks.entity.TaskPriority;
import com.devtogether.backend.tasks.entity.TaskStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid CreateTaskRequest should pass validation")
    void testValidCreateTaskRequest() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Create Architecture Diagram",
                "Detailed diagram for backend flow",
                1L,
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDate.now().plusDays(3)
        );

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CreateTaskRequest should reject blank title")
    void testRejectBlankTitle() {
        CreateTaskRequest request = new CreateTaskRequest(
                "   ",
                "Description",
                1L,
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null
        );

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    @DisplayName("CreateTaskRequest should reject title exceeding 150 characters")
    void testRejectTitleExceedingMaxLength() {
        String longTitle = "A".repeat(151);
        CreateTaskRequest request = new CreateTaskRequest(
                longTitle,
                "Description",
                1L,
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null
        );

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    @DisplayName("CreateTaskRequest should reject description exceeding 1000 characters")
    void testRejectDescriptionExceedingMaxLength() {
        String longDescription = "A".repeat(1001);
        CreateTaskRequest request = new CreateTaskRequest(
                "Valid Title",
                longDescription,
                1L,
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null
        );

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    @DisplayName("CreateTaskRequest should reject null userId")
    void testRejectNullUserId() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Valid Title",
                "Description",
                null,
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null
        );

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("userId"));
    }

    @Test
    @DisplayName("UpdateTaskRequest should reject blank title")
    void testUpdateTaskRequest_BlankTitle() {
        UpdateTaskRequest request = new UpdateTaskRequest(
                "",
                "Description",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                null
        );

        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    @DisplayName("UpdateTaskStatusRequest should reject null status")
    void testUpdateTaskStatusRequest_NullStatus() {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(null);

        Set<ConstraintViolation<UpdateTaskStatusRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("status"));
    }

    @Test
    @DisplayName("Valid UpdateTaskStatusRequest should pass validation")
    void testUpdateTaskStatusRequest_Valid() {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        Set<ConstraintViolation<UpdateTaskStatusRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
}
