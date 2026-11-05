package com.devtogether.backend.logs.repository;

import com.devtogether.backend.logs.entity.WorkLog;
import com.devtogether.backend.logs.entity.WorkLogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

    List<WorkLog> findAllByOrderByLogDateDesc();

    List<WorkLog> findByCategoryOrderByLogDateDesc(WorkLogCategory category);

    List<WorkLog> findByLogDateBetweenOrderByLogDateDesc(LocalDate startDate, LocalDate endDate);

    List<WorkLog> findByCategoryAndLogDateBetweenOrderByLogDateDesc(
            WorkLogCategory category,
            LocalDate startDate,
            LocalDate endDate
    );
}
