package com.todotask.backend.user.dao.dto;

import com.todotask.backend.user.dao.enums.Role;

public record UserResponse(Long id, String firstName, String lastName, String email, Role role) {
}
