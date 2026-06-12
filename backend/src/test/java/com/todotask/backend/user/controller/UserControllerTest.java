package com.todotask.backend.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.todotask.backend.core.exceptions.GlobalExceptionHandler;
import com.todotask.backend.user.dao.dto.UserRequest;
import com.todotask.backend.user.dao.dto.UserResponse;
import com.todotask.backend.user.dao.enums.Role;
import com.todotask.backend.user.exceptions.UserNotFoundException;
import com.todotask.backend.user.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
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
}
