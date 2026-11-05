package com.devtogether.backend.tasks.repository;

import com.devtogether.backend.tasks.entity.Task;
import com.devtogether.backend.tasks.entity.TaskPriority;
import com.devtogether.backend.tasks.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        task1 = new Task("Task 1", "Desc 1", 1L, TaskStatus.TODO, TaskPriority.LOW, LocalDate.now());
        task2 = new Task("Task 2", "Desc 2", 1L, TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, LocalDate.now().plusDays(1));
        task3 = new Task("Task 3", "Desc 3", 2L, TaskStatus.COMPLETED, TaskPriority.HIGH, LocalDate.now().plusDays(2));

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
    }

    @Test
    @DisplayName("Should persist and auto-generate ID, createdAt and updatedAt")
    void testPersistTask() {
        Task newTask = new Task("New Task", "Description", 3L, null, null, null);
        Task saved = taskRepository.save(newTask);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(saved.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("findByUserIdOrderByCreatedAtDesc should return tasks for the given user")
    void testFindByUserId() {
        List<Task> user1Tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(1L);

        assertThat(user1Tasks).hasSize(2);
        assertThat(user1Tasks).allMatch(t -> t.getUserId().equals(1L));

        List<Task> user2Tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(2L);
        assertThat(user2Tasks).hasSize(1);
        assertThat(user2Tasks.get(0).getTitle()).isEqualTo("Task 3");
    }

    @Test
    @DisplayName("findByUserIdAndStatusOrderByCreatedAtDesc should filter by both userId and status")
    void testFindByUserIdAndStatus() {
        List<Task> tasks = taskRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, TaskStatus.IN_PROGRESS);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Task 2");
        assertThat(tasks.get(0).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("findByStatusOrderByCreatedAtDesc should filter tasks by status across users")
    void testFindByStatus() {
        List<Task> completed = taskRepository.findByStatusOrderByCreatedAtDesc(TaskStatus.COMPLETED);

        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).getTitle()).isEqualTo("Task 3");
    }

    @Test
    @DisplayName("findAllByOrderByCreatedAtDesc should return all tasks")
    void testFindAll() {
        List<Task> all = taskRepository.findAllByOrderByCreatedAtDesc();

        assertThat(all).hasSize(3);
    }
}
