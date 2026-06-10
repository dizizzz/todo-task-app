package com.todotask.backend.task.service;

import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.dao.model.Task;
import com.todotask.backend.task.dao.repository.TaskRepository;
import com.todotask.backend.task.exceptions.TaskNotFoundException;
import com.todotask.backend.task.mapper.TaskMapper;
import com.todotask.backend.task.service.interfaces.TaskService;
import com.todotask.backend.user.dao.model.User;
import com.todotask.backend.user.dao.repository.UserRepository;
import com.todotask.backend.user.exceptions.UserNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
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
        Task task = new Task();
        task.setName(request.name());
        task.setPriority(request.priority());
        task.setState(request.state());
        task.setOwner(findUser(request.ownerId()));
        task.setCollaborators(findCollaborators(request.collaboratorIds()));
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
        task.setOwner(findUser(request.ownerId()));
        task.setCollaborators(findCollaborators(request.collaboratorIds()));
        Task updated = taskRepository.save(task);
        return taskMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Set<User> findCollaborators(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return ids.stream()
            .map(this::findUser)
            .collect(Collectors.toSet());
    }
}
