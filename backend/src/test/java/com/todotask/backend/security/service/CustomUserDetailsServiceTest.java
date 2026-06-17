package com.todotask.backend.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import com.todotask.backend.user.api.UserAuthInfo;
import com.todotask.backend.user.api.UserFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Verify loadUserByUsername returns user details")
    void loadUserByUsername_ShouldReturnUserDetails() {
        //given
        UserAuthInfo authInfo = new UserAuthInfo(1L, "mike@mail.com", "encodedPassword", "ADMIN");
        when(userFacade.getAuthInfoByEmail("mike@mail.com")).thenReturn(authInfo);

        //when
        UserDetails result = userDetailsService.loadUserByUsername("mike@mail.com");

        //then
        assertEquals("mike@mail.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Verify loadUserByUsername throws when user not found")
    void loadUserByUsername_WhenNotFound_ShouldThrow() {
        //given
        when(userFacade.getAuthInfoByEmail("nope@mail.com"))
            .thenThrow(new RuntimeException("not found"));

        //then
        assertThrows(UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("nope@mail.com"));
    }
}
