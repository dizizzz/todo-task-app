package com.todotask.backend.task.dao.dto;

import com.todotask.backend.task.dao.enums.Priority;
import com.todotask.backend.task.dao.enums.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record TaskRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotNull(message = "Priority is required")
    Priority priority,

    State state,

    Set<Long> collaboratorIds
) {
}
