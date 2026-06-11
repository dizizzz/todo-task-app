package com.todotask.backend.user.api;

import com.todotask.backend.user.dao.dto.UserResponse;
import com.todotask.backend.user.service.interfaces.UserService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;

    @Override
    public UserInfo getById(Long id) {
        return toInfo(userService.getById(id));
    }

    @Override
    public Set<UserInfo> getByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return ids.stream()
            .map(this::getById)
            .collect(Collectors.toSet());
    }

    private UserInfo toInfo(UserResponse user) {
        return new UserInfo(
            user.id(),
            user.firstName(),
            user.lastName(),
            user.email()
        );
    }
}
