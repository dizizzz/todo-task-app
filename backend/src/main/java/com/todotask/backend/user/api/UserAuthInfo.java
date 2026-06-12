package com.todotask.backend.user.api;

public record UserAuthInfo(
    String email,
    String password,
    String role
) {
}
