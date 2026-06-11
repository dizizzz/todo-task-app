package com.todotask.backend.task.dao.dto;

import com.todotask.backend.user.api.UserInfo;
import com.todotask.backend.task.dao.enums.Priority;
import com.todotask.backend.task.dao.enums.State;
import java.util.Set;

public record TaskResponse(Long id, String name, Priority priority, State state,
                           UserInfo owner, Set<UserInfo> collaborators) {
}
