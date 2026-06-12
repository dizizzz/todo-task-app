package com.todotask.backend.user.api;

public record UserAuthInfo(
    Long id,
    String email,
    String password,
    String role
) {
}
