package com.devtogether.backend.tasks.controller;

import com.devtogether.backend.exception.GlobalExceptionHandler;
import com.devtogether.backend.tasks.dto.CreateTaskRequest;
import com.devtogether.backend.tasks.dto.TaskResponse;
import com.devtogether.backend.tasks.dto.UpdateTaskRequest;
import com.devtogether.backend.tasks.dto.UpdateTaskStatusRequest;
import com.devtogether.backend.tasks.entity.TaskPriority;
import com.devtogether.backend.tasks.entity.TaskStatus;
import com.devtogether.backend.tasks.exception.TaskNotFoundException;
import com.devtogether.backend.tasks.service.TaskService;
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
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleResponse = new TaskResponse(
                1L,
                "Implement Task API",
                "Create controller and service endpoints",
                5L,
                TaskStatus.TODO,
                TaskPriority.HIGH,
                LocalDate.of(2026, 11, 10),
                LocalDateTime.of(2026, 11, 6, 10, 0),
                LocalDateTime.of(2026, 11, 6, 10, 0)
        );
    }

    @Test
    @DisplayName("POST /api/tasks should create task and return 201 Created")
    void testCreateTask_Success() throws Exception {
        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(sampleResponse);

        String jsonPayload = """
                {
                    "title": "Implement Task API",
                    "description": "Create controller and service endpoints",
                    "userId": 5,
                    "status": "TODO",
                    "priority": "HIGH",
                    "dueDate": "2026-11-10"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Implement Task API")))
                .andExpect(jsonPath("$.userId", is(5)))
                .andExpect(jsonPath("$.status", is("TODO")))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        verify(taskService, times(1)).createTask(any(CreateTaskRequest.class));
    }

    @Test
    @DisplayName("POST /api/tasks with invalid body should return 400 Bad Request")
    void testCreateTask_ValidationError() throws Exception {
        String invalidPayload = """
                {
                    "title": "",
                    "description": "Some description"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors", notNullValue()));

        verify(taskService, never()).createTask(any());
    }

    @Test
    @DisplayName("GET /api/tasks should return 200 OK and all tasks")
    void testGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Implement Task API")));
    }

    @Test
    @DisplayName("GET /api/tasks?status=TODO should filter tasks by status")
    void testGetAllTasks_FilteredByStatus() throws Exception {
        when(taskService.getTasksByStatus(TaskStatus.TODO)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/tasks").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("TODO")));

        verify(taskService, times(1)).getTasksByStatus(TaskStatus.TODO);
    }

    @Test
    @DisplayName("GET /api/tasks/{id} should return 200 OK when found")
    void testGetTaskById_Found() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Implement Task API")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} should return 404 Not Found when ID does not exist")
    void testGetTaskById_NotFound() throws Exception {
        when(taskService.getTaskById(999L)).thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId} should return tasks for user")
    void testGetTasksByUserId() throws Exception {
        when(taskService.getTasksByUserId(5L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/tasks/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(5)));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId}?status=TODO should filter user tasks by status")
    void testGetTasksByUserIdAndStatus() throws Exception {
        when(taskService.getTasksByUserIdAndStatus(5L, TaskStatus.TODO)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/tasks/user/5").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("TODO")));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} should update task and return 200 OK")
    void testUpdateTask_Success() throws Exception {
        when(taskService.updateTask(eq(1L), any(UpdateTaskRequest.class))).thenReturn(sampleResponse);

        String updatePayload = """
                {
                    "title": "Updated Task Title",
                    "description": "Updated Description",
                    "status": "IN_PROGRESS",
                    "priority": "LOW",
                    "dueDate": "2026-11-15"
                }
                """;

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} should return 404 Not Found when task does not exist")
    void testUpdateTask_NotFound() throws Exception {
        when(taskService.updateTask(eq(999L), any(UpdateTaskRequest.class)))
                .thenThrow(new TaskNotFoundException(999L));

        String updatePayload = """
                {
                    "title": "Valid Title",
                    "description": "Valid Description",
                    "status": "IN_PROGRESS",
                    "priority": "LOW",
                    "dueDate": "2026-11-15"
                }
                """;

        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status should update status and return 200 OK")
    void testUpdateTaskStatus_Success() throws Exception {
        TaskResponse completedResponse = new TaskResponse(
                1L,
                "Implement Task API",
                "Create controller and service endpoints",
                5L,
                TaskStatus.COMPLETED,
                TaskPriority.HIGH,
                LocalDate.of(2026, 11, 10),
                LocalDateTime.of(2026, 11, 6, 10, 0),
                LocalDateTime.of(2026, 11, 6, 11, 0)
        );

        when(taskService.updateTaskStatus(eq(1L), any(UpdateTaskStatusRequest.class)))
                .thenReturn(completedResponse);

        String patchPayload = """
                {
                    "status": "COMPLETED"
                }
                """;

        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status with missing status should return 400 Bad Request")
    void testUpdateTaskStatus_MissingStatus() throws Exception {
        String patchPayload = """
                {}
                """;

        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status should return 404 when task missing")
    void testUpdateTaskStatus_NotFound() throws Exception {
        when(taskService.updateTaskStatus(eq(999L), any(UpdateTaskStatusRequest.class)))
                .thenThrow(new TaskNotFoundException(999L));

        String patchPayload = """
                {
                    "status": "IN_PROGRESS"
                }
                """;

        mockMvc.perform(patch("/api/tasks/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchPayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} should return 204 No Content when successful")
    void testDeleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} should return 404 Not Found when ID does not exist")
    void testDeleteTask_NotFound() throws Exception {
        doThrow(new TaskNotFoundException(999L)).when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
