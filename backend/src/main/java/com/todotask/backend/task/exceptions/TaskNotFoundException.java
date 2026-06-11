package com.todotask.backend.task.exceptions;

import com.todotask.backend.core.exceptions.NotFoundException;

public class TaskNotFoundException extends NotFoundException {
    public TaskNotFoundException(Long id) {
        super("Task not found with id: " + id);
    }
}
