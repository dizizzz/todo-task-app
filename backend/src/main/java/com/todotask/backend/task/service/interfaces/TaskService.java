package com.todotask.backend.task.service.interfaces;

import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponse create(TaskRequest request, Long currentUserId);

    Page<TaskResponse> getAll(Pageable pageable, Long currentUserId);

    TaskResponse getById(Long id, Long currentUserId);

    TaskResponse update(Long id, TaskRequest request, Long currentUserId);

    void delete(Long id, Long currentUserId);

    Page<TaskResponse> getByUser(Long userId, Pageable pageable);
}
