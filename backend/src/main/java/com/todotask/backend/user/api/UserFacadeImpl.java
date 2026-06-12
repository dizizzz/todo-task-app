package com.todotask.backend.user.api;

import com.todotask.backend.user.dao.dto.UserResponse;
import com.todotask.backend.user.dao.model.User;
import com.todotask.backend.user.dao.repository.UserRepository;
import com.todotask.backend.user.exceptions.UserNotFoundException;
import com.todotask.backend.user.service.interfaces.UserService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;
    private final UserRepository userRepository;

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

    @Override
    public UserAuthInfo getAuthInfoByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(null));
        return new UserAuthInfo(
            user.getEmail(),
            user.getPassword(),
            user.getRole().name()
        );
    }
}
