package com.taskflow.service;

import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public List<TaskResponse> findByProject(Long projectId, String email) {
        projectService.getEntity(projectId, email);
        return taskRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    public TaskResponse create(Long projectId, TaskRequest request, String email) {
        Project project = projectService.getEntity(projectId, email);
        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .project(project)
                .build();
        if (request.status() != null) task.setStatus(request.status());
        if (request.priority() != null) task.setPriority(request.priority());
        return toResponse(taskRepository.save(task));
    }

    public TaskResponse update(Long taskId, TaskRequest request, String email) {
        Task task = getEntity(taskId, email);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        if (request.status() != null) task.setStatus(request.status());
        if (request.priority() != null) task.setPriority(request.priority());
        return toResponse(task);
    }

    public TaskResponse updateStatus(Long taskId, TaskStatus status, String email) {
        Task task = getEntity(taskId, email);
        task.setStatus(status);
        return toResponse(task);
    }

    public void delete(Long taskId, String email) {
        taskRepository.delete(getEntity(taskId, email));
    }

    private Task getEntity(Long id, String email) {
        return taskRepository.findByIdAndProjectOwnerEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable : " + id));
    }

    private TaskResponse toResponse(Task t) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(),
                t.getPriority(), t.getDueDate(), t.getProject().getId(), t.getCreatedAt());
    }
}