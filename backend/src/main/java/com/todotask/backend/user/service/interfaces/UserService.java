package com.todotask.backend.user.service.interfaces;

import com.todotask.backend.user.dao.dto.UserRequest;
import com.todotask.backend.user.dao.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse create(UserRequest request);

    Page<UserResponse> getAll(Pageable pageable);

    UserResponse getById(Long id);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}
