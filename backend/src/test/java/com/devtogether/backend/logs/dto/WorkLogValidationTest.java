package com.devtogether.backend.logs.dto;

import com.devtogether.backend.logs.entity.WorkLogCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WorkLogValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid CreateWorkLogRequest should pass validation")
    void testValidCreateRequest() {
        CreateWorkLogRequest request = new CreateWorkLogRequest(
                "Implemented Feature",
                "Detailed description of work",
                WorkLogCategory.DEVELOPMENT,
                LocalDate.now(),
                new BigDecimal("6.50")
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CreateWorkLogRequest should reject blank title")
    void testRejectBlankTitle() {
        CreateWorkLogRequest request = new CreateWorkLogRequest(
                "   ",
                "Description",
                WorkLogCategory.DEVELOPMENT,
                LocalDate.now(),
                new BigDecimal("4.00")
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    @DisplayName("CreateWorkLogRequest should reject title exceeding 150 characters")
    void testRejectTitleExceedingMaxLength() {
        String longTitle = "A".repeat(151);
        CreateWorkLogRequest request = new CreateWorkLogRequest(
                longTitle,
                "Description",
                WorkLogCategory.DEVELOPMENT,
                LocalDate.now(),
                new BigDecimal("4.00")
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    @DisplayName("CreateWorkLogRequest should reject description exceeding 1000 characters")
    void testRejectDescriptionExceedingMaxLength() {
        String longDescription = "A".repeat(1001);
        CreateWorkLogRequest request = new CreateWorkLogRequest(
                "Valid Title",
                longDescription,
                WorkLogCategory.DEVELOPMENT,
                LocalDate.now(),
                new BigDecimal("4.00")
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    @DisplayName("CreateWorkLogRequest should reject null category")
    void testRejectNullCategory() {
        CreateWorkLogRequest request = new CreateWorkLogRequest(
                "Valid Title",
                "Description",
                null,
                LocalDate.now(),
                new BigDecimal("4.00")
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("category"));
    }

    @Test
    @DisplayName("CreateWorkLogRequest should reject null logDate")
    void testRejectNullLogDate() {
        CreateWorkLogRequest request = new CreateWorkLogRequest(
                "Valid Title",
                "Description",
                WorkLogCategory.RESEARCH,
                null,
                new BigDecimal("4.00")
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("logDate"));
    }

    @Test
    @DisplayName("CreateWorkLogRequest should reject zero or negative hoursSpent")
    void testRejectZeroOrNegativeHours() {
        CreateWorkLogRequest zeroHours = new CreateWorkLogRequest(
                "Valid Title",
                "Description",
                WorkLogCategory.TESTING,
                LocalDate.now(),
                BigDecimal.ZERO
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> zeroViolations = validator.validate(zeroHours);
        assertThat(zeroViolations).anyMatch(v -> v.getPropertyPath().toString().equals("hoursSpent"));

        CreateWorkLogRequest negativeHours = new CreateWorkLogRequest(
                "Valid Title",
                "Description",
                WorkLogCategory.TESTING,
                LocalDate.now(),
                new BigDecimal("-1.50")
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> negativeViolations = validator.validate(negativeHours);
        assertThat(negativeViolations).anyMatch(v -> v.getPropertyPath().toString().equals("hoursSpent"));
    }

    @Test
    @DisplayName("CreateWorkLogRequest should reject hoursSpent greater than 24")
    void testRejectHoursGreaterThan24() {
        CreateWorkLogRequest request = new CreateWorkLogRequest(
                "Valid Title",
                "Description",
                WorkLogCategory.PLANNING,
                LocalDate.now(),
                new BigDecimal("24.01")
        );

        Set<ConstraintViolation<CreateWorkLogRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("hoursSpent"));
    }

    @Test
    @DisplayName("UpdateWorkLogRequest should also enforce all constraints")
    void testUpdateWorkLogRequestConstraints() {
        UpdateWorkLogRequest invalidRequest = new UpdateWorkLogRequest(
                "",
                null,
                null,
                null,
                new BigDecimal("25.0")
        );

        Set<ConstraintViolation<UpdateWorkLogRequest>> violations = validator.validate(invalidRequest);
        assertThat(violations).hasSize(4); // title, category, logDate, hoursSpent
    }
}
