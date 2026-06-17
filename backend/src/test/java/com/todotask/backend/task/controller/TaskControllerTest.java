package com.todotask.backend.task.controller;

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
import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.dao.enums.Priority;
import com.todotask.backend.task.dao.enums.State;
import com.todotask.backend.task.exceptions.TaskNotFoundException;
import com.todotask.backend.task.service.interfaces.TaskService;
import com.todotask.backend.user.api.UserInfo;
import java.util.Set;
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
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Long CURRENT_USER_ID = 1L;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(taskController)
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
        TaskRequest request = new TaskRequest("Task #1", Priority.HIGH, State.NEW, Set.of());
        UserInfo owner = new UserInfo(1L, "Mike", "Brown", "mike@mail.com");
        TaskResponse response = new TaskResponse(1L, "Task #1", Priority.HIGH, State.NEW, owner, Set.of());
        when(taskService.create(any(TaskRequest.class), eq(CURRENT_USER_ID))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Task #1"))
            .andExpect(jsonPath("$.owner.id").value(1));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        TaskRequest invalid = new TaskRequest("", null, State.NEW, Set.of());

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getById_ShouldReturnOk_WhenTaskExists() throws Exception {
        UserInfo owner = new UserInfo(1L, "Mike", "Brown", "mike@mail.com");
        TaskResponse response = new TaskResponse(1L, "Task #1", Priority.HIGH, State.NEW, owner, Set.of());
        when(taskService.getById(eq(1L), eq(CURRENT_USER_ID))).thenReturn(response);

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Task #1"));
    }

    @Test
    void getById_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        when(taskService.getById(eq(99L), eq(CURRENT_USER_ID))).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void update_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        TaskRequest request = new TaskRequest("Updated Task", Priority.MEDIUM, State.DOING, Set.of());
        UserInfo owner = new UserInfo(1L, "Mike", "Brown", "mike@mail.com");
        TaskResponse response =
            new TaskResponse(1L, "Updated Task", Priority.MEDIUM, State.DOING, owner, Set.of());
        when(taskService.update(eq(1L), any(TaskRequest.class), eq(CURRENT_USER_ID)))
            .thenReturn(response);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Task"))
            .andExpect(jsonPath("$.state").value("DOING"));
    }
}
