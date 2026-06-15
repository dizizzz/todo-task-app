package com.todotask.backend.user.dao.dto;

import com.todotask.backend.user.dao.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UserAdminUpdateRequest(
    @NotNull(message = "Role is required")
    Role role
) {
}
