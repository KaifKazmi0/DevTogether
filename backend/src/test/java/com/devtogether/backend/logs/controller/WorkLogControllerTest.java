package com.devtogether.backend.logs.controller;

import com.devtogether.backend.exception.GlobalExceptionHandler;
import com.devtogether.backend.logs.dto.CreateWorkLogRequest;
import com.devtogether.backend.logs.dto.UpdateWorkLogRequest;
import com.devtogether.backend.logs.dto.WorkLogResponse;
import com.devtogether.backend.logs.entity.WorkLogCategory;
import com.devtogether.backend.logs.exception.InvalidDateRangeException;
import com.devtogether.backend.logs.exception.WorkLogNotFoundException;
import com.devtogether.backend.logs.service.WorkLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WorkLogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WorkLogService workLogService;

    @InjectMocks
    private WorkLogController workLogController;

    private WorkLogResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(workLogController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleResponse = new WorkLogResponse(
                1L,
                "Implemented Navigation",
                "Built responsive sidebar",
                WorkLogCategory.DEVELOPMENT,
                LocalDate.of(2026, 11, 5),
                new BigDecimal("3.50"),
                LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 10, 0)
        );
    }

    @Test
    @DisplayName("POST /api/work-logs should create work log and return 201 Created")
    void testCreateWorkLog_Success() throws Exception {
        when(workLogService.createWorkLog(any(CreateWorkLogRequest.class))).thenReturn(sampleResponse);

        String jsonPayload = """
                {
                    "title": "Implemented Navigation",
                    "description": "Built responsive sidebar",
                    "category": "DEVELOPMENT",
                    "logDate": "2026-11-05",
                    "hoursSpent": 3.50
                }
                """;

        mockMvc.perform(post("/api/work-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Implemented Navigation")))
                .andExpect(jsonPath("$.category", is("DEVELOPMENT")))
                .andExpect(jsonPath("$.hoursSpent", is(3.50)));

        verify(workLogService, times(1)).createWorkLog(any(CreateWorkLogRequest.class));
    }

    @Test
    @DisplayName("POST /api/work-logs with invalid body should return 400 Bad Request with validation errors")
    void testCreateWorkLog_ValidationError() throws Exception {
        String invalidPayload = """
                {
                    "title": "",
                    "description": "Some description",
                    "category": "DEVELOPMENT",
                    "logDate": "2026-11-05",
                    "hoursSpent": -1.0
                }
                """;

        mockMvc.perform(post("/api/work-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors", notNullValue()));

        verify(workLogService, never()).createWorkLog(any());
    }

    @Test
    @DisplayName("POST /api/work-logs with hours > 24 should return 400 Bad Request")
    void testCreateWorkLog_HoursExceedsMax() throws Exception {
        String invalidPayload = """
                {
                    "title": "Valid Title",
                    "description": "Some description",
                    "category": "DEVELOPMENT",
                    "logDate": "2026-11-05",
                    "hoursSpent": 24.50
                }
                """;

        mockMvc.perform(post("/api/work-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors[*].field", hasItem("hoursSpent")));
    }

    @Test
    @DisplayName("GET /api/work-logs should return 200 OK and all work logs")
    void testGetAllWorkLogs() throws Exception {
        when(workLogService.filterWorkLogs(null, null, null)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/work-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Implemented Navigation")));
    }

    @Test
    @DisplayName("GET /api/work-logs?category=DEVELOPMENT should filter by category")
    void testFilterByCategory() throws Exception {
        when(workLogService.filterWorkLogs(eq(WorkLogCategory.DEVELOPMENT), isNull(), isNull()))
                .thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/work-logs").param("category", "DEVELOPMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("DEVELOPMENT")));
    }

    @Test
    @DisplayName("GET /api/work-logs?category=INVALID should return 400 Bad Request")
    void testFilterByInvalidCategory() throws Exception {
        mockMvc.perform(get("/api/work-logs").param("category", "NON_EXISTENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Invalid category value")));
    }

    @Test
    @DisplayName("GET /api/work-logs?startDate=2026-11-01&endDate=2026-11-05 should filter by date range")
    void testFilterByDateRange() throws Exception {
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 5);

        when(workLogService.filterWorkLogs(isNull(), eq(start), eq(end)))
                .thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/work-logs")
                        .param("startDate", "2026-11-01")
                        .param("endDate", "2026-11-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/work-logs with startDate after endDate should return 400 Bad Request")
    void testFilterByInvalidDateRange() throws Exception {
        LocalDate start = LocalDate.of(2026, 11, 10);
        LocalDate end = LocalDate.of(2026, 11, 5);

        when(workLogService.filterWorkLogs(isNull(), eq(start), eq(end)))
                .thenThrow(new InvalidDateRangeException("Start date cannot be after end date"));

        mockMvc.perform(get("/api/work-logs")
                        .param("startDate", "2026-11-10")
                        .param("endDate", "2026-11-05"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Start date cannot be after end date")));
    }

    @Test
    @DisplayName("GET /api/work-logs/{id} should return 200 OK when found")
    void testGetWorkLogById_Found() throws Exception {
        when(workLogService.getWorkLogById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/work-logs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Implemented Navigation")));
    }

    @Test
    @DisplayName("GET /api/work-logs/{id} should return 404 Not Found when ID missing")
    void testGetWorkLogById_NotFound() throws Exception {
        when(workLogService.getWorkLogById(999L)).thenThrow(new WorkLogNotFoundException(999L));

        mockMvc.perform(get("/api/work-logs/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    @DisplayName("PUT /api/work-logs/{id} should update and return 200 OK")
    void testUpdateWorkLog_Success() throws Exception {
        when(workLogService.updateWorkLog(eq(1L), any(UpdateWorkLogRequest.class))).thenReturn(sampleResponse);

        String updatePayload = """
                {
                    "title": "Implemented Navigation Updated",
                    "description": "Updated description",
                    "category": "DEVELOPMENT",
                    "logDate": "2026-11-05",
                    "hoursSpent": 4.00
                }
                """;

        mockMvc.perform(put("/api/work-logs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PUT /api/work-logs/{id} should return 404 Not Found when missing")
    void testUpdateWorkLog_NotFound() throws Exception {
        when(workLogService.updateWorkLog(eq(999L), any(UpdateWorkLogRequest.class)))
                .thenThrow(new WorkLogNotFoundException(999L));

        String updatePayload = """
                {
                    "title": "Valid Title",
                    "description": "Valid description",
                    "category": "DEVELOPMENT",
                    "logDate": "2026-11-05",
                    "hoursSpent": 4.00
                }
                """;

        mockMvc.perform(put("/api/work-logs/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("DELETE /api/work-logs/{id} should return 204 No Content")
    void testDeleteWorkLog_Success() throws Exception {
        doNothing().when(workLogService).deleteWorkLog(1L);

        mockMvc.perform(delete("/api/work-logs/1"))
                .andExpect(status().isNoContent());

        verify(workLogService, times(1)).deleteWorkLog(1L);
    }

    @Test
    @DisplayName("DELETE /api/work-logs/{id} should return 404 Not Found when ID missing")
    void testDeleteWorkLog_NotFound() throws Exception {
        doThrow(new WorkLogNotFoundException(999L)).when(workLogService).deleteWorkLog(999L);

        mockMvc.perform(delete("/api/work-logs/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
