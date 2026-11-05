package com.devtogether.backend.logs.controller;

import com.devtogether.backend.logs.dto.CreateWorkLogRequest;
import com.devtogether.backend.logs.dto.UpdateWorkLogRequest;
import com.devtogether.backend.logs.dto.WorkLogResponse;
import com.devtogether.backend.logs.entity.WorkLogCategory;
import com.devtogether.backend.logs.service.WorkLogService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work-logs")
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkLogResponse createWorkLog(@Valid @RequestBody CreateWorkLogRequest request) {
        return workLogService.createWorkLog(request);
    }

    @GetMapping
    public List<WorkLogResponse> getWorkLogs(
            @RequestParam(required = false) WorkLogCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return workLogService.filterWorkLogs(category, startDate, endDate);
    }

    @GetMapping("/{id}")
    public WorkLogResponse getWorkLogById(@PathVariable Long id) {
        return workLogService.getWorkLogById(id);
    }

    @PutMapping("/{id}")
    public WorkLogResponse updateWorkLog(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkLogRequest request
    ) {
        return workLogService.updateWorkLog(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkLog(@PathVariable Long id) {
        workLogService.deleteWorkLog(id);
    }
}
