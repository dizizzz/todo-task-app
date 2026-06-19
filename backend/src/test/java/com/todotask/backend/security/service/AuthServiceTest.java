package com.todotask.backend.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import com.todotask.backend.security.dto.LoginRequest;
import com.todotask.backend.security.dto.LoginResponse;
import com.todotask.backend.security.jwt.JwtUtil;
import com.todotask.backend.user.api.UserAuthInfo;
import com.todotask.backend.user.api.UserFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Verify login returns token on valid credentials")
    void login_ShouldReturnToken() {
        //given
        LoginRequest request = new LoginRequest("mike@mail.com", "secret123");
        UserAuthInfo authInfo = new UserAuthInfo(1L, "mike@mail.com", "encodedPassword", "USER");
        when(userFacade.getAuthInfoByEmail("mike@mail.com")).thenReturn(authInfo);
        when(jwtUtil.generateToken("mike@mail.com", 1L, "USER")).thenReturn("token123");
        //when
        LoginResponse result = authService.login(request);

        //then
        assertEquals("token123", result.token());
    }
}
