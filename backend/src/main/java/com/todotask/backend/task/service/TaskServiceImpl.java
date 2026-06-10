package com.todotask.backend.task.service;

import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.dao.repository.TaskRepository;
import com.todotask.backend.task.mapper.TaskMapper;
import com.todotask.backend.task.service.interfaces.TaskService;
import com.todotask.backend.user.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse create(TaskRequest request) {
        return null;
    }

    @Override
    public Page<TaskResponse> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public TaskResponse getById(Long id) {
        return null;
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
