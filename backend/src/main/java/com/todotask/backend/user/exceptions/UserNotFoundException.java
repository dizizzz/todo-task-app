package com.todotask.backend.user.exceptions;

import com.todotask.backend.core.exceptions.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
}
