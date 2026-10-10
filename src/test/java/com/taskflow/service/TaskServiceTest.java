package com.taskflow.service;

import com.taskflow.dto.TaskResponse;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock ProjectService projectService;
    @InjectMocks TaskService taskService;

    @Test
    void updateStatusMovesTheTask() {
        Task task = Task.builder().id(5L).title("Maquettes")
                .project(Project.builder().id(1L).build()).build();
        when(taskRepository.findByIdAndProjectOwnerEmail(5L, "a@test.com"))
                .thenReturn(Optional.of(task));

        TaskResponse res = taskService.updateStatus(5L, TaskStatus.IN_PROGRESS, "a@test.com");

        assertEquals(TaskStatus.IN_PROGRESS, res.status());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void updateStatusFailsForForeignTask() {
        when(taskRepository.findByIdAndProjectOwnerEmail(5L, "bob@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateStatus(5L, TaskStatus.DONE, "bob@test.com"));
    }
}
