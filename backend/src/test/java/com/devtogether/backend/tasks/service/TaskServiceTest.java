package com.devtogether.backend.tasks.service;

import com.devtogether.backend.tasks.dto.CreateTaskRequest;
import com.devtogether.backend.tasks.dto.TaskResponse;
import com.devtogether.backend.tasks.dto.UpdateTaskRequest;
import com.devtogether.backend.tasks.dto.UpdateTaskStatusRequest;
import com.devtogether.backend.tasks.entity.Task;
import com.devtogether.backend.tasks.entity.TaskPriority;
import com.devtogether.backend.tasks.entity.TaskStatus;
import com.devtogether.backend.tasks.exception.TaskNotFoundException;
import com.devtogether.backend.tasks.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Implement Task Entity");
        sampleTask.setDescription("Create JPA entity and mappings");
        sampleTask.setUserId(10L);
        sampleTask.setStatus(TaskStatus.TODO);
        sampleTask.setPriority(TaskPriority.HIGH);
        sampleTask.setDueDate(LocalDate.of(2026, 11, 10));
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("createTask should persist and return mapped TaskResponse")
    void testCreateTask() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Implement Task Entity",
                "Create JPA entity and mappings",
                10L,
                TaskStatus.TODO,
                TaskPriority.HIGH,
                LocalDate.of(2026, 11, 10)
        );

        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.createTask(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Implement Task Entity");
        assertThat(response.userId()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
        assertThat(response.priority()).isEqualTo(TaskPriority.HIGH);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask should default status to TODO and priority to MEDIUM when null")
    void testCreateTask_DefaultValues() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Implement Task Entity",
                null,
                10L,
                null,
                null,
                null
        );

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task argument = invocation.getArgument(0);
            argument.setId(2L);
            return argument;
        });

        TaskResponse response = taskService.createTask(request);

        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
        assertThat(response.priority()).isEqualTo(TaskPriority.MEDIUM);
    }

    @Test
    @DisplayName("getAllTasks should return all tasks sorted by createdAt descending")
    void testGetAllTasks() {
        when(taskRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleTask));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).title()).isEqualTo("Implement Task Entity");
        verify(taskRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("getTaskById should return TaskResponse when task exists")
    void testGetTaskById_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponse response = taskService.getTaskById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getTaskById should throw TaskNotFoundException when task does not exist")
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("getTasksByUserId should return tasks associated with user ID")
    void testGetTasksByUserId() {
        when(taskRepository.findByUserIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(sampleTask));

        List<TaskResponse> responses = taskService.getTasksByUserId(10L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).userId()).isEqualTo(10L);
        verify(taskRepository, times(1)).findByUserIdOrderByCreatedAtDesc(10L);
    }

    @Test
    @DisplayName("getTasksByUserIdAndStatus should return tasks matching user ID and status")
    void testGetTasksByUserIdAndStatus() {
        when(taskRepository.findByUserIdAndStatusOrderByCreatedAtDesc(10L, TaskStatus.TODO))
                .thenReturn(List.of(sampleTask));

        List<TaskResponse> responses = taskService.getTasksByUserIdAndStatus(10L, TaskStatus.TODO);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).status()).isEqualTo(TaskStatus.TODO);
        verify(taskRepository, times(1)).findByUserIdAndStatusOrderByCreatedAtDesc(10L, TaskStatus.TODO);
    }

    @Test
    @DisplayName("getTasksByStatus should return tasks matching given status")
    void testGetTasksByStatus() {
        when(taskRepository.findByStatusOrderByCreatedAtDesc(TaskStatus.TODO))
                .thenReturn(List.of(sampleTask));

        List<TaskResponse> responses = taskService.getTasksByStatus(TaskStatus.TODO);

        assertThat(responses).hasSize(1);
        verify(taskRepository, times(1)).findByStatusOrderByCreatedAtDesc(TaskStatus.TODO);
    }

    @Test
    @DisplayName("updateTask should update fields and save when task exists")
    void testUpdateTask_Success() {
        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated Task Title",
                "Updated Description",
                TaskStatus.IN_PROGRESS,
                TaskPriority.LOW,
                LocalDate.of(2026, 11, 20)
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.updateTask(1L, request);

        assertThat(response).isNotNull();
        assertThat(sampleTask.getTitle()).isEqualTo("Updated Task Title");
        assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(sampleTask.getPriority()).isEqualTo(TaskPriority.LOW);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("updateTask should throw TaskNotFoundException when task does not exist")
    void testUpdateTask_NotFound() {
        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated Title",
                "Description",
                TaskStatus.COMPLETED,
                TaskPriority.HIGH,
                null
        );

        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(999L, request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTaskStatus should modify only the task status")
    void testUpdateTaskStatus_Success() {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.updateTaskStatus(1L, request);

        assertThat(response).isNotNull();
        assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("updateTaskStatus should throw TaskNotFoundException when task is missing")
    void testUpdateTaskStatus_NotFound() {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTaskStatus(999L, request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask should delete task when ID exists")
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteTask should throw TaskNotFoundException when ID is missing")
    void testDeleteTask_NotFound() {
        when(taskRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
        verify(taskRepository, never()).deleteById(anyLong());
    }
}
