package com.devtogether.backend.tasks.repository;

import com.devtogether.backend.tasks.entity.Task;
import com.devtogether.backend.tasks.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    List<Task> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, TaskStatus status);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);
}
