package com.devtogether.backend.logs.service;

import com.devtogether.backend.logs.dto.CreateWorkLogRequest;
import com.devtogether.backend.logs.dto.UpdateWorkLogRequest;
import com.devtogether.backend.logs.dto.WorkLogResponse;
import com.devtogether.backend.logs.entity.WorkLog;
import com.devtogether.backend.logs.entity.WorkLogCategory;
import com.devtogether.backend.logs.exception.InvalidDateRangeException;
import com.devtogether.backend.logs.exception.WorkLogNotFoundException;
import com.devtogether.backend.logs.repository.WorkLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkLogService {

    private final WorkLogRepository workLogRepository;

    public WorkLogService(WorkLogRepository workLogRepository) {
        this.workLogRepository = workLogRepository;
    }

    @Transactional
    public WorkLogResponse createWorkLog(CreateWorkLogRequest request) {
        WorkLog workLog = new WorkLog();
        workLog.setTitle(request.title().trim());
        workLog.setDescription(request.description() != null ? request.description().trim() : null);
        workLog.setCategory(request.category());
        workLog.setLogDate(request.logDate());
        workLog.setHoursSpent(request.hoursSpent());

        WorkLog savedWorkLog = workLogRepository.save(workLog);
        return toResponse(savedWorkLog);
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponse> getAllWorkLogs() {
        return workLogRepository.findAllByOrderByLogDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkLogResponse getWorkLogById(Long id) {
        WorkLog workLog = workLogRepository.findById(id)
                .orElseThrow(() -> new WorkLogNotFoundException(id));
        return toResponse(workLog);
    }

    @Transactional
    public WorkLogResponse updateWorkLog(Long id, UpdateWorkLogRequest request) {
        WorkLog workLog = workLogRepository.findById(id)
                .orElseThrow(() -> new WorkLogNotFoundException(id));

        workLog.setTitle(request.title().trim());
        workLog.setDescription(request.description() != null ? request.description().trim() : null);
        workLog.setCategory(request.category());
        workLog.setLogDate(request.logDate());
        workLog.setHoursSpent(request.hoursSpent());

        WorkLog updatedWorkLog = workLogRepository.save(workLog);
        return toResponse(updatedWorkLog);
    }

    @Transactional
    public void deleteWorkLog(Long id) {
        if (!workLogRepository.existsById(id)) {
            throw new WorkLogNotFoundException(id);
        }
        workLogRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponse> getWorkLogsByCategory(WorkLogCategory category) {
        return workLogRepository.findByCategoryOrderByLogDateDesc(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponse> getWorkLogsByDateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return workLogRepository.findByLogDateBetweenOrderByLogDateDesc(startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponse> getWorkLogsByCategoryAndDateRange(
            WorkLogCategory category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        validateDateRange(startDate, endDate);
        return workLogRepository.findByCategoryAndLogDateBetweenOrderByLogDateDesc(category, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponse> filterWorkLogs(
            WorkLogCategory category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new InvalidDateRangeException("Both startDate and endDate must be provided for date range filtering.");
        }

        if (category != null && startDate != null) {
            return getWorkLogsByCategoryAndDateRange(category, startDate, endDate);
        } else if (category != null) {
            return getWorkLogsByCategory(category);
        } else if (startDate != null) {
            return getWorkLogsByDateRange(startDate, endDate);
        } else {
            return getAllWorkLogs();
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidDateRangeException("Start date and end date must not be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date (" + startDate + ") cannot be after end date (" + endDate + ").");
        }
    }

    public WorkLogResponse toResponse(WorkLog workLog) {
        return new WorkLogResponse(
                workLog.getId(),
                workLog.getTitle(),
                workLog.getDescription(),
                workLog.getCategory(),
                workLog.getLogDate(),
                workLog.getHoursSpent(),
                workLog.getCreatedAt(),
                workLog.getUpdatedAt()
        );
    }
}
