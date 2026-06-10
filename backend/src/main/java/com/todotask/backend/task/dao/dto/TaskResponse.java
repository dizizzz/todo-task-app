package com.todotask.backend.task.dao.dto;

import com.todotask.backend.task.dao.enums.Priority;
import com.todotask.backend.task.dao.enums.State;
import com.todotask.backend.user.dao.dto.UserResponse;
import java.util.Set;

public record TaskResponse(Long id, String name, Priority priority, State state,
                           UserResponse owner, Set<UserResponse> collaborators) {
}
