package com.todotask.backend.task.service;

import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.dao.model.Task;
import com.todotask.backend.task.dao.repository.TaskRepository;
import com.todotask.backend.task.exceptions.TaskNotFoundException;
import com.todotask.backend.task.mapper.TaskMapper;
import com.todotask.backend.task.service.interfaces.TaskService;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        task.setName(request.name());
        task.setPriority(request.priority());
        task.setState(request.state());
        task.setOwnerId(request.ownerId());
        task.setCollaboratorIds(
            request.collaboratorIds() == null ? new HashSet<>() : request.collaboratorIds()
        );
        Task saved = taskRepository.save(task);
        return taskMapper.toResponse(saved);
    }

    @Override
    public Page<TaskResponse> getAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
            .map(taskMapper::toResponse);
    }

    @Override
    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        task.setName(request.name());
        task.setPriority(request.priority());
        task.setState(request.state());
        task.setOwnerId(request.ownerId());
        task.setCollaboratorIds(
            request.collaboratorIds() == null ? new HashSet<>() : request.collaboratorIds()
        );
        Task updated = taskRepository.save(task);
        return taskMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
