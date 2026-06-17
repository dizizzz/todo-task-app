package com.todotask.backend.user.service.interfaces;

import com.todotask.backend.user.api.UserInfo;
import com.todotask.backend.user.dao.dto.UserAdminUpdateRequest;
import com.todotask.backend.user.dao.dto.UserRequest;
import com.todotask.backend.user.dao.dto.UserResponse;
import com.todotask.backend.user.dao.dto.UserSelfUpdateRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse create(UserRequest request);

    Page<UserResponse> getAll(Pageable pageable);

    UserResponse getById(Long id);

    UserResponse updateByAdmin(Long id, UserAdminUpdateRequest request);

    void delete(Long id);

    List<UserInfo> getAllForCollaborators(Long currentUserId);

    UserResponse updateSelf(Long currentUserId, UserSelfUpdateRequest request);
}
