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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserFacade userFacade;

    @Override
    public TaskResponse create(TaskRequest request, Long currentUserId) {
        Task task = new Task();
        task.setName(request.name());
        task.setPriority(request.priority());
        task.setState(request.state());
        task.setOwnerId(currentUserId);
        Set<Long> collaborators =
            request.collaboratorIds() == null ? new HashSet<>() : new HashSet<>(request.collaboratorIds());
        collaborators.remove(currentUserId);
        task.setCollaboratorIds(collaborators);
        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Override
    public Page<TaskResponse> getAll(Pageable pageable, Long currentUserId) {
        return taskRepository.findByOwnerOrCollaborator(currentUserId, pageable)
            .map(this::toResponse);
    }

    @Override
    public TaskResponse getById(Long id, Long currentUserId) {
        Task task = findOwnedTask(id, currentUserId);
        return toResponse(task);
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request, Long currentUserId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        boolean isOwner = task.getOwnerId().equals(currentUserId);
        boolean isCollaborator = task.getCollaboratorIds().contains(currentUserId);
        if (!isOwner && !isCollaborator) {
            throw new AccessDeniedException("You can only modify tasks you own or collaborate on");
        }
        task.setName(request.name());
        task.setPriority(request.priority());
        task.setState(request.state());
        Set<Long> collaborators =
            request.collaboratorIds() == null ? new HashSet<>() : new HashSet<>(request.collaboratorIds());
        collaborators.remove(task.getOwnerId());
        task.setCollaboratorIds(collaborators);
        Task updated = taskRepository.save(task);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        if (!task.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only delete your own tasks");
        }
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

    private Task findOwnedTask(Long id, Long currentUserId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        boolean isOwner = task.getOwnerId().equals(currentUserId);
        boolean isCollaborator = task.getCollaboratorIds().contains(currentUserId);
        if (!isOwner && !isCollaborator) {
            throw new AccessDeniedException("You do not have access to this task");
        }
        return task;
    }

    @Override
    public Page<TaskResponse> getByUser(Long userId, Pageable pageable) {
        return taskRepository.findByOwnerOrCollaborator(userId, pageable)
            .map(this::toResponse);
    }
}
