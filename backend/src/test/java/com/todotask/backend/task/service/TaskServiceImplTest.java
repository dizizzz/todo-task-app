package com.todotask.backend.task.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.dao.enums.Priority;
import com.todotask.backend.task.dao.enums.State;
import com.todotask.backend.task.dao.model.Task;
import com.todotask.backend.task.dao.repository.TaskRepository;
import com.todotask.backend.task.exceptions.TaskNotFoundException;
import com.todotask.backend.task.mapper.TaskMapper;
import com.todotask.backend.user.api.UserFacade;
import com.todotask.backend.user.api.UserInfo;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskRequest request;
    private TaskResponse mapperResponse;
    private UserInfo owner;

    private static final Long CURRENT_USER_ID = 1L;

    @BeforeEach
    public void setup() {
        task = new Task();
        task.setId(1L);
        task.setName("Task #1");
        task.setPriority(Priority.HIGH);
        task.setState(State.NEW);
        task.setOwnerId(1L);
        task.setCollaboratorIds(Set.of());

        request = new TaskRequest("Task #1", Priority.HIGH, State.NEW,  Set.of());

        // те, що повертає mapper (owner/collaborators у ньому ignore -> null)
        mapperResponse = new TaskResponse(1L, "Task #1", Priority.HIGH, State.NEW, null, null);

        owner = new UserInfo(1L, "Mike", "Brown", "mike@mail.com");
    }

    @Test
    void create_WhenRequestIsValid_ShouldSaveTaskAndFillOwner() {
        //when
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(mapperResponse);
        when(userFacade.getById(1L)).thenReturn(owner);
        when(userFacade.getByIds(Set.of())).thenReturn(Set.of());

        TaskResponse result = taskService.create(request, CURRENT_USER_ID);

        //then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Task #1", result.name());
        assertEquals(owner, result.owner());
    }

    @Test
    void getById_WhenTaskExists_ShouldReturnTask() {
        //when
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(mapperResponse);
        when(userFacade.getById(1L)).thenReturn(owner);
        when(userFacade.getByIds(Set.of())).thenReturn(Set.of());

        TaskResponse result = taskService.getById(1L, CURRENT_USER_ID);

        //then
        assertNotNull(result);
        assertEquals(owner, result.owner());
    }

    @Test
    void getById_WhenTaskDoesNotExist_ShouldThrowException() {
        //when
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        //then
        assertThrows(TaskNotFoundException.class,
            () -> taskService.getById(99L, CURRENT_USER_ID));
    }

    @Test
    void getById_WhenUserIsNotOwnerOrCollaborator_ShouldThrowAccessDenied() {
        //given
        Task otherTask = new Task();
        otherTask.setId(5L);
        otherTask.setOwnerId(99L);
        otherTask.setCollaboratorIds(Set.of());

        //when
        when(taskRepository.findById(5L)).thenReturn(Optional.of(otherTask));

        //then
        assertThrows(AccessDeniedException.class,
            () -> taskService.getById(5L, CURRENT_USER_ID));
    }

    @Test
    void update_WhenTaskDoesNotExist_ShouldThrowException() {
        //when
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        //then
        assertThrows(TaskNotFoundException.class,
            () -> taskService.update(99L, request, CURRENT_USER_ID));
    }

    @Test
    void update_WhenUserIsNotOwnerOrCollaborator_ShouldThrowAccessDenied() {
        //given
        Task otherTask = new Task();
        otherTask.setId(5L);
        otherTask.setOwnerId(99L);
        otherTask.setCollaboratorIds(Set.of());

        //when
        when(taskRepository.findById(5L)).thenReturn(Optional.of(otherTask));

        //then
        assertThrows(AccessDeniedException.class,
            () -> taskService.update(5L, request, CURRENT_USER_ID));
    }

    @Test
    void delete_WhenCalled_ShouldCallRepository() {
        //when
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        taskService.delete(1L, CURRENT_USER_ID);

        //then
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void update_WhenUserIsCollaborator_ShouldUpdateTask() {
        //given
        Task collabTask = new Task();
        collabTask.setId(7L);
        collabTask.setName("Old");
        collabTask.setPriority(Priority.LOW);
        collabTask.setState(State.NEW);
        collabTask.setOwnerId(99L);
        collabTask.setCollaboratorIds(new java.util.HashSet<>(Set.of(CURRENT_USER_ID)));

        when(taskRepository.findById(7L)).thenReturn(Optional.of(collabTask));
        when(taskRepository.save(any(Task.class))).thenReturn(collabTask);
        when(taskMapper.toResponse(collabTask)).thenReturn(mapperResponse);
        when(userFacade.getById(99L)).thenReturn(owner);
        when(userFacade.getByIds(any())).thenReturn(Set.of());

        //when
        TaskResponse result = taskService.update(7L, request, CURRENT_USER_ID);

        //then
        assertNotNull(result);
    }

    @Test
    void create_ShouldRemoveOwnerFromCollaborators() {
        //given
        TaskRequest withOwnerAsCollab =
            new TaskRequest("Task", Priority.HIGH, State.NEW, Set.of(CURRENT_USER_ID));
        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setOwnerId(CURRENT_USER_ID);
        savedTask.setCollaboratorIds(new java.util.HashSet<>());

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(mapperResponse);
        when(userFacade.getById(CURRENT_USER_ID)).thenReturn(owner);
        when(userFacade.getByIds(any())).thenReturn(Set.of());

        //when
        taskService.create(withOwnerAsCollab, CURRENT_USER_ID);

        //then
        org.mockito.ArgumentCaptor<Task> captor = org.mockito.ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertFalse(captor.getValue().getCollaboratorIds().contains(CURRENT_USER_ID));
    }

    @Test
    void delete_WhenUserIsCollaborator_ShouldThrowAccessDenied() {
        //given
        Task collabTask = new Task();
        collabTask.setId(7L);
        collabTask.setOwnerId(99L);
        collabTask.setCollaboratorIds(new java.util.HashSet<>(Set.of(CURRENT_USER_ID)));

        when(taskRepository.findById(7L)).thenReturn(Optional.of(collabTask));

        //then колаборатор не вид
        assertThrows(AccessDeniedException.class,
            () -> taskService.delete(7L, CURRENT_USER_ID));
    }
}
