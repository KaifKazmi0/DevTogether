package com.devtogether.backend.logs.repository;

import com.devtogether.backend.logs.entity.WorkLog;
import com.devtogether.backend.logs.entity.WorkLogCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WorkLogRepositoryTest {

    @Autowired
    private WorkLogRepository workLogRepository;

    private WorkLog log1;
    private WorkLog log2;
    private WorkLog log3;

    @BeforeEach
    void setUp() {
        workLogRepository.deleteAll();

        log1 = new WorkLog("Dev Task 1", "Implemented UI", WorkLogCategory.DEVELOPMENT, LocalDate.of(2026, 11, 1), new BigDecimal("4.00"));
        log2 = new WorkLog("Bug Fix Task", "Fixed crash", WorkLogCategory.BUG_FIX, LocalDate.of(2026, 11, 3), new BigDecimal("2.50"));
        log3 = new WorkLog("Dev Task 2", "Refactored API", WorkLogCategory.DEVELOPMENT, LocalDate.of(2026, 11, 5), new BigDecimal("5.00"));

        workLogRepository.save(log1);
        workLogRepository.save(log2);
        workLogRepository.save(log3);
    }

    @Test
    @DisplayName("Should persist and generate ID and timestamps")
    void testPersistWorkLog() {
        WorkLog newLog = new WorkLog("Documentation", "Wrote API docs", WorkLogCategory.DOCUMENTATION, LocalDate.of(2026, 11, 4), new BigDecimal("1.50"));
        WorkLog saved = workLogRepository.save(newLog);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Documentation");
    }

    @Test
    @DisplayName("findAllByOrderByLogDateDesc should return all records in descending date order")
    void testFindAllByOrderByLogDateDesc() {
        List<WorkLog> results = workLogRepository.findAllByOrderByLogDateDesc();

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getLogDate()).isEqualTo(LocalDate.of(2026, 11, 5));
        assertThat(results.get(1).getLogDate()).isEqualTo(LocalDate.of(2026, 11, 3));
        assertThat(results.get(2).getLogDate()).isEqualTo(LocalDate.of(2026, 11, 1));
    }

    @Test
    @DisplayName("findByCategoryOrderByLogDateDesc should return only records matching the given category")
    void testFindByCategoryOrderByLogDateDesc() {
        List<WorkLog> devLogs = workLogRepository.findByCategoryOrderByLogDateDesc(WorkLogCategory.DEVELOPMENT);

        assertThat(devLogs).hasSize(2);
        assertThat(devLogs).allMatch(l -> l.getCategory() == WorkLogCategory.DEVELOPMENT);
        assertThat(devLogs.get(0).getLogDate()).isEqualTo(LocalDate.of(2026, 11, 5));
        assertThat(devLogs.get(1).getLogDate()).isEqualTo(LocalDate.of(2026, 11, 1));

        List<WorkLog> bugLogs = workLogRepository.findByCategoryOrderByLogDateDesc(WorkLogCategory.BUG_FIX);
        assertThat(bugLogs).hasSize(1);
        assertThat(bugLogs.get(0).getTitle()).isEqualTo("Bug Fix Task");
    }

    @Test
    @DisplayName("findByLogDateBetweenOrderByLogDateDesc should return records within the specified date range")
    void testFindByLogDateBetweenOrderByLogDateDesc() {
        LocalDate start = LocalDate.of(2026, 11, 2);
        LocalDate end = LocalDate.of(2026, 11, 4);

        List<WorkLog> results = workLogRepository.findByLogDateBetweenOrderByLogDateDesc(start, end);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLogDate()).isEqualTo(LocalDate.of(2026, 11, 3));
    }

    @Test
    @DisplayName("findByCategoryAndLogDateBetweenOrderByLogDateDesc should combine category and date range filters")
    void testFindByCategoryAndLogDateBetweenOrderByLogDateDesc() {
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 4);

        List<WorkLog> results = workLogRepository.findByCategoryAndLogDateBetweenOrderByLogDateDesc(
                WorkLogCategory.DEVELOPMENT,
                start,
                end
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Dev Task 1");
    }
}
