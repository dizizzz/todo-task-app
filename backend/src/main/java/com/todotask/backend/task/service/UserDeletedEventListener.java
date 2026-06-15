package com.todotask.backend.task.service;

import com.todotask.backend.task.dao.model.Task;
import com.todotask.backend.task.dao.repository.TaskRepository;
import com.todotask.backend.user.api.UserDeletedEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDeletedEventListener {
    private final TaskRepository taskRepository;

    @ApplicationModuleListener
    public void onUserDeleted(UserDeletedEvent event) {
        List<Task> tasks = taskRepository.findByCollaborator(event.userId());
        for (Task task : tasks) {
            task.getCollaboratorIds().remove(event.userId());
        }
        taskRepository.saveAll(tasks);
        taskRepository.deleteByOwnerId(event.userId());
    }
}
