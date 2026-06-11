package com.todotask.backend.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.todotask.backend.core.exceptions.GlobalExceptionHandler;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(taskController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void create_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        TaskRequest request = new TaskRequest("Task #1", Priority.HIGH, State.NEW, 1L, Set.of());
        UserInfo owner = new UserInfo(1L, "Mike", "Brown", "mike@mail.com");
        TaskResponse response = new TaskResponse(1L, "Task #1", Priority.HIGH, State.NEW, owner, Set.of());
        when(taskService.create(any(TaskRequest.class))).thenReturn(response);

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
        TaskRequest invalid = new TaskRequest("", null, State.NEW, null, Set.of());

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getById_ShouldReturnOk_WhenTaskExists() throws Exception {
        UserInfo owner = new UserInfo(1L, "Mike", "Brown", "mike@mail.com");
        TaskResponse response = new TaskResponse(1L, "Task #1", Priority.HIGH, State.NEW, owner, Set.of());
        when(taskService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Task #1"));
    }

    @Test
    void getById_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        when(taskService.getById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());
    }
}
