package com.taskflow.controller;

import com.taskflow.dto.AssignRequest;
import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.dto.TaskStatusRequest;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> findByProject(@PathVariable Long projectId, Authentication auth) {
        return taskService.findByProject(projectId, auth.getName());
    }

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@PathVariable Long projectId,
                               @Valid @RequestBody TaskRequest request, Authentication auth) {
        return taskService.create(projectId, request, auth.getName());
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse update(@PathVariable Long id,
                               @Valid @RequestBody TaskRequest request, Authentication auth) {
        return taskService.update(id, request, auth.getName());
    }

    @PatchMapping("/tasks/{id}/status")
    public TaskResponse updateStatus(@PathVariable Long id,
                                     @Valid @RequestBody TaskStatusRequest request, Authentication auth) {
        return taskService.updateStatus(id, request.status(), auth.getName());
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        taskService.delete(id, auth.getName());
    }

    @PatchMapping("/tasks/{id}/assignee")
    public TaskResponse assign(@PathVariable Long id,
                               @RequestBody AssignRequest request,
                               Authentication auth) {
        return taskService.assign(id, request.userId(), auth.getName());
    }
}