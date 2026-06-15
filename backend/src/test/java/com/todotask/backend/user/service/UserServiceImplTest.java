package com.todotask.backend.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.todotask.backend.user.api.UserDeletedEvent;
import com.todotask.backend.user.dao.dto.UserRequest;
import com.todotask.backend.user.dao.dto.UserResponse;
import com.todotask.backend.user.dao.enums.Role;
import com.todotask.backend.user.dao.model.User;
import com.todotask.backend.user.dao.repository.UserRepository;
import com.todotask.backend.user.exceptions.UserNotFoundException;
import com.todotask.backend.user.mapper.UserMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequest request;
    private UserResponse userResponse;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Mike");
        user.setLastName("Brown");
        user.setEmail("mike@mail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        request = new UserRequest("Mike", "Brown", "mike@mail.com", "secret123");

        userResponse = new UserResponse(1L, "Mike", "Brown", "mike@mail.com", Role.USER);
    }

    @Test
    @DisplayName("Verify user creation encodes password and sets USER role")
    void create_WhenRequestIsValid_ShouldEncodePasswordAndSetRole() {
        //when
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("secret123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.create(request);

        //then
        assertNotNull(result);
        assertEquals(userResponse, result);
        assertEquals(Role.USER, user.getRole());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    @DisplayName("Verify getById with incorrect id throws exception")
    void getById_WhenUserDoesNotExist_ShouldThrowException() {
        //when
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        //then
        assertThrows(UserNotFoundException.class,
            () -> userService.getById(99L));
    }

    @Test
    @DisplayName("Verify getById with correct id returns user")
    void getById_WhenUserExists_ShouldReturnUser() {
        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getById(1L);

        //then
        assertNotNull(result);
        assertEquals(userResponse, result);
    }

    @Test
    @DisplayName("Verify getAll returns page of users")
    void getAll_WhenCalled_ShouldReturnPageOfUsers() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        //when
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAll(pageable);

        //then
        assertEquals(1, result.getTotalElements());
        assertEquals(userResponse, result.getContent().getFirst());
    }

    @Test
    @DisplayName("Verify update with correct id updates user")
    void update_WhenUserExists_ShouldUpdateUser() {
        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("secret123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.update(1L, request);

        //then
        assertNotNull(result);
        assertEquals(userResponse, result);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    @DisplayName("Verify update with incorrect id throws exception")
    void update_WhenUserDoesNotExist_ShouldThrowException() {
        //when
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        //then
        assertThrows(UserNotFoundException.class,
            () -> userService.update(99L, request));
    }

    @Test
    @DisplayName("Verify delete calls repository deleteById and publishes event")
    void delete_WhenCalled_ShouldCallRepositoryAndPublishEvent() {
        //when
        userService.delete(1L);

        //then
        verify(userRepository).deleteById(1L);
        verify(eventPublisher).publishEvent(new UserDeletedEvent(1L));
    }
}
