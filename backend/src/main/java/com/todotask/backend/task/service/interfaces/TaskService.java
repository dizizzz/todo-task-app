package com.todotask.backend.task.service.interfaces;

import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponse create(TaskRequest request);

    Page<TaskResponse> getAll(Pageable pageable);

    TaskResponse getById(Long id);

    TaskResponse update(Long id, TaskRequest request);

    void delete(Long id);
}
