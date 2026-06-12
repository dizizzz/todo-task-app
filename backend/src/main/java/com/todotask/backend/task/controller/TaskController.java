package com.todotask.backend.task.controller;

import com.todotask.backend.core.security.AuthenticatedUser;
import com.todotask.backend.task.dao.dto.TaskRequest;
import com.todotask.backend.task.dao.dto.TaskResponse;
import com.todotask.backend.task.service.interfaces.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request,
                                               @AuthenticationPrincipal AuthenticatedUser currentUser) {
        TaskResponse response = taskService.create(request, currentUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAll(Pageable pageable,
                                                     @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(taskService.getAll(pageable, currentUser.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id,
                                                @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(taskService.getById(id, currentUser.id()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody TaskRequest request,
                                               @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(taskService.update(id, request, currentUser.id()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal AuthenticatedUser currentUser) {
        taskService.delete(id, currentUser.id());
        return ResponseEntity.noContent().build();
    }
}
