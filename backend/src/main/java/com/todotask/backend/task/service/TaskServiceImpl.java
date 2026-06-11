package com.todotask.backend.task.service;

import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.dao.model.Task;
import com.todotask.backend.task.dao.repository.TaskRepository;
import com.todotask.backend.task.exceptions.TaskNotFoundException;
import com.todotask.backend.task.mapper.TaskMapper;
import com.todotask.backend.task.service.interfaces.TaskService;
import com.todotask.backend.user.api.UserFacade;
import com.todotask.backend.user.api.UserInfo;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserFacade userFacade;

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
        return toResponse(saved);
    }

    @Override
    public Page<TaskResponse> getAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
            .map(this::toResponse);
    }

    @Override
    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return toResponse(task);
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
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {
        UserInfo owner = userFacade.getById(task.getOwnerId());
        Set<UserInfo> collaborators = userFacade.getByIds(task.getCollaboratorIds());
        TaskResponse base = taskMapper.toResponse(task);
        return new TaskResponse(
            base.id(),
            base.name(),
            base.priority(),
            base.state(),
            owner,
            collaborators
        );
    }
}
