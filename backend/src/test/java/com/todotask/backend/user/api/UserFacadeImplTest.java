package com.todotask.backend.user.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import com.todotask.backend.user.dao.dto.UserResponse;
import com.todotask.backend.user.dao.enums.Role;
import com.todotask.backend.user.dao.model.User;
import com.todotask.backend.user.dao.repository.UserRepository;
import com.todotask.backend.user.exceptions.UserNotFoundException;
import com.todotask.backend.user.service.interfaces.UserService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserFacadeImplTest {
    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserFacadeImpl userFacade;

    @Test
    @DisplayName("Verify getById returns user info")
    void getById_ShouldReturnUserInfo() {
        //given
        UserResponse response = new UserResponse(1L, "Mike", "Brown", "mike@mail.com", Role.USER);
        when(userService.getById(1L)).thenReturn(response);

        //when
        UserInfo result = userFacade.getById(1L);

        //then
        assertEquals(1L, result.id());
        assertEquals("Mike", result.firstName());
        assertEquals("mike@mail.com", result.email());
    }

    @Test
    @DisplayName("Verify getByIds returns empty set for null or empty input")
    void getByIds_WhenEmpty_ShouldReturnEmptySet() {
        //then
        assertTrue(userFacade.getByIds(null).isEmpty());
        assertTrue(userFacade.getByIds(Set.of()).isEmpty());
    }

    @Test
    @DisplayName("Verify getByIds returns info for given ids")
    void getByIds_ShouldReturnUserInfos() {
        //given
        UserResponse response = new UserResponse(1L, "Mike", "Brown", "mike@mail.com", Role.USER);
        when(userService.getById(1L)).thenReturn(response);

        //when
        Set<UserInfo> result = userFacade.getByIds(Set.of(1L));

        //then
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Verify getAuthInfoByEmail returns auth info")
    void getAuthInfoByEmail_ShouldReturnAuthInfo() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("mike@mail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.ADMIN);
        when(userRepository.findByEmail("mike@mail.com")).thenReturn(Optional.of(user));

        //when
        UserAuthInfo result = userFacade.getAuthInfoByEmail("mike@mail.com");

        //then
        assertEquals(1L, result.id());
        assertEquals("mike@mail.com", result.email());
        assertEquals("encodedPassword", result.password());
        assertEquals("ADMIN", result.role());
    }

    @Test
    @DisplayName("Verify getAuthInfoByEmail throws when user not found")
    void getAuthInfoByEmail_WhenNotFound_ShouldThrow() {
        //given
        when(userRepository.findByEmail("nope@mail.com")).thenReturn(Optional.empty());

        //then
        assertThrows(UserNotFoundException.class,
            () -> userFacade.getAuthInfoByEmail("nope@mail.com"));
    }
}
