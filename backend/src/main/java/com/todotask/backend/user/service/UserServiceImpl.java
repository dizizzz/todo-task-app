package com.todotask.backend.user.service;

import com.todotask.backend.user.api.UserDeletedEvent;
import com.todotask.backend.user.api.UserInfo;
import com.todotask.backend.user.dao.dto.UserAdminUpdateRequest;
import com.todotask.backend.user.dao.dto.UserRequest;
import com.todotask.backend.user.dao.dto.UserResponse;
import com.todotask.backend.user.dao.enums.Role;
import com.todotask.backend.user.dao.model.User;
import com.todotask.backend.user.dao.repository.UserRepository;
import com.todotask.backend.user.exceptions.UserNotFoundException;
import com.todotask.backend.user.mapper.UserMapper;
import com.todotask.backend.user.service.interfaces.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UserResponse create(UserRequest request) {
        User user = userMapper.toEntity(request);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toResponse);
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateByAdmin(Long id, UserAdminUpdateRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        user.setRole(request.role());
        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
        eventPublisher.publishEvent(new UserDeletedEvent(id));
    }

    @Override
    public List<UserInfo> getAllForCollaborators(Long currentUserId) {
        return userRepository.findAll().stream()
            .filter(user -> !user.getId().equals(currentUserId))
            .map(user -> new UserInfo(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()))
            .toList();
    }

    @Override
    public UserResponse updateSelf(Long currentUserId, UserRequest request) {
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new UserNotFoundException(currentUserId));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }
}
