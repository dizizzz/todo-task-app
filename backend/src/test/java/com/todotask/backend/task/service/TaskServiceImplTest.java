package com.todotask.backend.task.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @BeforeEach
    public void setup() {
        task = new Task();
        task.setId(1L);
        task.setName("Task #1");
        task.setPriority(Priority.HIGH);
        task.setState(State.NEW);
        task.setOwnerId(1L);
        task.setCollaboratorIds(Set.of());

        request = new TaskRequest("Task #1", Priority.HIGH, State.NEW, 1L, Set.of());

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

        TaskResponse result = taskService.create(request);

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

        TaskResponse result = taskService.getById(1L);

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
            () -> taskService.getById(99L));
    }

    @Test
    void update_WhenTaskDoesNotExist_ShouldThrowException() {
        //when
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        //then
        assertThrows(TaskNotFoundException.class,
            () -> taskService.update(99L, request));
    }

    @Test
    void delete_WhenCalled_ShouldCallRepository() {
        //when
        taskService.delete(1L);

        //then
        verify(taskRepository).deleteById(1L);
    }
}
