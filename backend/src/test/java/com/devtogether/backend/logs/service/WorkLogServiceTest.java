package com.devtogether.backend.logs.service;

import com.devtogether.backend.logs.dto.CreateWorkLogRequest;
import com.devtogether.backend.logs.dto.UpdateWorkLogRequest;
import com.devtogether.backend.logs.dto.WorkLogResponse;
import com.devtogether.backend.logs.entity.WorkLog;
import com.devtogether.backend.logs.entity.WorkLogCategory;
import com.devtogether.backend.logs.exception.InvalidDateRangeException;
import com.devtogether.backend.logs.exception.WorkLogNotFoundException;
import com.devtogether.backend.logs.repository.WorkLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkLogServiceTest {

    @Mock
    private WorkLogRepository workLogRepository;

    @InjectMocks
    private WorkLogService workLogService;

    private WorkLog sampleWorkLog;

    @BeforeEach
    void setUp() {
        sampleWorkLog = new WorkLog();
        sampleWorkLog.setId(1L);
        sampleWorkLog.setTitle("Initial Setup");
        sampleWorkLog.setDescription("Setup repository and basic config");
        sampleWorkLog.setCategory(WorkLogCategory.DEVELOPMENT);
        sampleWorkLog.setLogDate(LocalDate.of(2026, 11, 5));
        sampleWorkLog.setHoursSpent(new BigDecimal("4.50"));
        sampleWorkLog.setCreatedAt(LocalDateTime.now());
        sampleWorkLog.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("createWorkLog should persist and return mapped WorkLogResponse")
    void testCreateWorkLog() {
        CreateWorkLogRequest request = new CreateWorkLogRequest(
                "Initial Setup",
                "Setup repository and basic config",
                WorkLogCategory.DEVELOPMENT,
                LocalDate.of(2026, 11, 5),
                new BigDecimal("4.50")
        );

        when(workLogRepository.save(any(WorkLog.class))).thenReturn(sampleWorkLog);

        WorkLogResponse response = workLogService.createWorkLog(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Initial Setup");
        assertThat(response.category()).isEqualTo(WorkLogCategory.DEVELOPMENT);
        assertThat(response.hoursSpent()).isEqualTo(new BigDecimal("4.50"));
        verify(workLogRepository, times(1)).save(any(WorkLog.class));
    }

    @Test
    @DisplayName("getAllWorkLogs should return all logs ordered by logDate descending")
    void testGetAllWorkLogs() {
        when(workLogRepository.findAllByOrderByLogDateDesc()).thenReturn(List.of(sampleWorkLog));

        List<WorkLogResponse> responses = workLogService.getAllWorkLogs();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).title()).isEqualTo("Initial Setup");
        verify(workLogRepository, times(1)).findAllByOrderByLogDateDesc();
    }

    @Test
    @DisplayName("getWorkLogById should return the log when existing ID is provided")
    void testGetWorkLogById_Found() {
        when(workLogRepository.findById(1L)).thenReturn(Optional.of(sampleWorkLog));

        WorkLogResponse response = workLogService.getWorkLogById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        verify(workLogRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getWorkLogById should throw WorkLogNotFoundException when ID is missing")
    void testGetWorkLogById_NotFound() {
        when(workLogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workLogService.getWorkLogById(999L))
                .isInstanceOf(WorkLogNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("updateWorkLog should modify fields and save when ID exists")
    void testUpdateWorkLog_Success() {
        UpdateWorkLogRequest request = new UpdateWorkLogRequest(
                "Updated Title",
                "Updated description",
                WorkLogCategory.BUG_FIX,
                LocalDate.of(2026, 11, 6),
                new BigDecimal("2.00")
        );

        when(workLogRepository.findById(1L)).thenReturn(Optional.of(sampleWorkLog));
        when(workLogRepository.save(any(WorkLog.class))).thenReturn(sampleWorkLog);

        WorkLogResponse response = workLogService.updateWorkLog(1L, request);

        assertThat(response).isNotNull();
        assertThat(sampleWorkLog.getTitle()).isEqualTo("Updated Title");
        assertThat(sampleWorkLog.getCategory()).isEqualTo(WorkLogCategory.BUG_FIX);
        verify(workLogRepository, times(1)).save(sampleWorkLog);
    }

    @Test
    @DisplayName("updateWorkLog should throw WorkLogNotFoundException when ID is missing")
    void testUpdateWorkLog_NotFound() {
        UpdateWorkLogRequest request = new UpdateWorkLogRequest(
                "Updated Title",
                "Updated description",
                WorkLogCategory.BUG_FIX,
                LocalDate.of(2026, 11, 6),
                new BigDecimal("2.00")
        );

        when(workLogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workLogService.updateWorkLog(999L, request))
                .isInstanceOf(WorkLogNotFoundException.class)
                .hasMessageContaining("999");
        verify(workLogRepository, never()).save(any(WorkLog.class));
    }

    @Test
    @DisplayName("deleteWorkLog should delete from repository when ID exists")
    void testDeleteWorkLog_Success() {
        when(workLogRepository.existsById(1L)).thenReturn(true);

        workLogService.deleteWorkLog(1L);

        verify(workLogRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteWorkLog should throw WorkLogNotFoundException when ID is missing")
    void testDeleteWorkLog_NotFound() {
        when(workLogRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> workLogService.deleteWorkLog(999L))
                .isInstanceOf(WorkLogNotFoundException.class)
                .hasMessageContaining("999");
        verify(workLogRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("getWorkLogsByCategory should filter repository by category")
    void testGetWorkLogsByCategory() {
        when(workLogRepository.findByCategoryOrderByLogDateDesc(WorkLogCategory.DEVELOPMENT))
                .thenReturn(List.of(sampleWorkLog));

        List<WorkLogResponse> responses = workLogService.getWorkLogsByCategory(WorkLogCategory.DEVELOPMENT);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).category()).isEqualTo(WorkLogCategory.DEVELOPMENT);
        verify(workLogRepository, times(1)).findByCategoryOrderByLogDateDesc(WorkLogCategory.DEVELOPMENT);
    }

    @Test
    @DisplayName("getWorkLogsByDateRange should filter repository between valid dates")
    void testGetWorkLogsByDateRange() {
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 5);

        when(workLogRepository.findByLogDateBetweenOrderByLogDateDesc(start, end))
                .thenReturn(List.of(sampleWorkLog));

        List<WorkLogResponse> responses = workLogService.getWorkLogsByDateRange(start, end);

        assertThat(responses).hasSize(1);
        verify(workLogRepository, times(1)).findByLogDateBetweenOrderByLogDateDesc(start, end);
    }

    @Test
    @DisplayName("getWorkLogsByDateRange should throw InvalidDateRangeException when startDate is after endDate")
    void testGetWorkLogsByDateRange_InvalidRange() {
        LocalDate start = LocalDate.of(2026, 11, 10);
        LocalDate end = LocalDate.of(2026, 11, 5);

        assertThatThrownBy(() -> workLogService.getWorkLogsByDateRange(start, end))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("cannot be after end date");
        verify(workLogRepository, never()).findByLogDateBetweenOrderByLogDateDesc(any(), any());
    }

    @Test
    @DisplayName("filterWorkLogs should throw InvalidDateRangeException when only one date is specified")
    void testFilterWorkLogs_PartialDateRange() {
        LocalDate start = LocalDate.of(2026, 11, 1);

        assertThatThrownBy(() -> workLogService.filterWorkLogs(null, start, null))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("Both startDate and endDate must be provided");
    }

    @Test
    @DisplayName("filterWorkLogs should combine category and date range filters")
    void testFilterWorkLogs_CategoryAndDateRange() {
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 5);

        when(workLogRepository.findByCategoryAndLogDateBetweenOrderByLogDateDesc(WorkLogCategory.DEVELOPMENT, start, end))
                .thenReturn(List.of(sampleWorkLog));

        List<WorkLogResponse> responses = workLogService.filterWorkLogs(WorkLogCategory.DEVELOPMENT, start, end);

        assertThat(responses).hasSize(1);
        verify(workLogRepository, times(1))
                .findByCategoryAndLogDateBetweenOrderByLogDateDesc(WorkLogCategory.DEVELOPMENT, start, end);
    }
}
