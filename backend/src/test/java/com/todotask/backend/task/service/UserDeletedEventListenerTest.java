package com.todotask.backend.task.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.todotask.backend.task.dao.model.Task;
import com.todotask.backend.task.dao.repository.TaskRepository;
import com.todotask.backend.user.api.UserDeletedEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserDeletedEventListenerTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserDeletedEventListener listener;

    @Test
    @DisplayName("Verify deleted user is removed from task collaborators and saved")
    void onUserDeleted_ShouldRemoveUserFromCollaborators() {
        //given
        Long deletedUserId = 5L;
        Task task = new Task();
        task.setId(1L);
        task.setOwnerId(2L);
        Set<Long> collaborators = new HashSet<>();
        collaborators.add(5L);
        collaborators.add(7L);
        task.setCollaboratorIds(collaborators);

        when(taskRepository.findByCollaborator(deletedUserId))
            .thenReturn(List.of(task));

        //when
        listener.onUserDeleted(new UserDeletedEvent(deletedUserId));

        //then
        assertFalse(task.getCollaboratorIds().contains(5L)); // видаленого прибрано
        assertTrue(task.getCollaboratorIds().contains(7L));  // той лишився
        verify(taskRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Verify tasks owned by deleted user are removed")
    void onUserDeleted_ShouldDeleteOwnedTasks() {
        //given
        Long deletedUserId = 5L;
        when(taskRepository.findByCollaborator(deletedUserId))
            .thenReturn(List.of());

        //when
        listener.onUserDeleted(new UserDeletedEvent(deletedUserId));

        //then
        verify(taskRepository).deleteByOwnerId(deletedUserId); // задачі вл вид
    }
}
