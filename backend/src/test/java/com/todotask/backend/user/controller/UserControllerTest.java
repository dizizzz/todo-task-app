package com.todotask.backend.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.todotask.backend.core.exceptions.GlobalExceptionHandler;
import com.todotask.backend.core.security.AuthenticatedUser;
import com.todotask.backend.user.api.UserInfo;
import com.todotask.backend.user.dao.dto.UserAdminUpdateRequest;
import com.todotask.backend.user.dao.dto.UserRequest;
import com.todotask.backend.user.dao.dto.UserResponse;
import com.todotask.backend.user.dao.dto.UserSelfUpdateRequest;
import com.todotask.backend.user.dao.enums.Role;
import com.todotask.backend.user.exceptions.UserNotFoundException;
import com.todotask.backend.user.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Long CURRENT_USER_ID = 1L;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(MethodParameter parameter) {
                    return parameter.getParameterType().equals(AuthenticatedUser.class);
                }

                @Override
                public Object resolveArgument(MethodParameter parameter,
                                              ModelAndViewContainer mavContainer,
                                              NativeWebRequest webRequest,
                                              WebDataBinderFactory binderFactory) {
                    return new AuthenticatedUser(CURRENT_USER_ID, "mike@mail.com");
                }
            })
            .build();
    }

    @Test
    void create_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        UserRequest request = new UserRequest("Mike", "Brown", "mike@mail.com", "secret123");
        UserResponse response = new UserResponse(1L, "Mike", "Brown", "mike@mail.com", Role.USER);
        when(userService.create(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("mike@mail.com"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        UserRequest invalid = new UserRequest("", "", "not-an-email", "");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getById_ShouldReturnOk_WhenUserExists() throws Exception {
        UserResponse response = new UserResponse(1L, "Mike", "Brown", "mike@mail.com", Role.USER);
        when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Mike"));
    }

    @Test
    void getById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(userService.getById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void updateByAdmin_ShouldReturnOk() throws Exception {
        UserAdminUpdateRequest request = new UserAdminUpdateRequest(Role.ADMIN);
        UserResponse response = new UserResponse(1L, "Mike", "Brown", "mike@mail.com", Role.ADMIN);
        when(userService.updateByAdmin(eq(1L), any(UserAdminUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void updateSelf_ShouldReturnOk() throws Exception {
        UserSelfUpdateRequest request =
            new UserSelfUpdateRequest("Mike", "Brown", "mike@mail.com", "secret123");
        UserResponse response = new UserResponse(1L, "Mike", "Brown", "mike@mail.com", Role.USER);
        when(userService.updateSelf(eq(CURRENT_USER_ID), any(UserSelfUpdateRequest.class)))
            .thenReturn(response);

        mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("mike@mail.com"));
    }

    @Test
    void getCollaborators_ShouldReturnOk() throws Exception {
        UserInfo info = new UserInfo(2L, "User", "One", "user1@mail.com");
        when(userService.getAllForCollaborators(CURRENT_USER_ID))
            .thenReturn(java.util.List.of(info));

        mockMvc.perform(get("/api/users/collaborators"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("user1@mail.com"));
    }
}
