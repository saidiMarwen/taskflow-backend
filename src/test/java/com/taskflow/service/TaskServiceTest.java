package com.taskflow.service;

import com.taskflow.dto.TaskResponse;
import com.taskflow.event.TaskAssignedEvent;
import com.taskflow.event.TaskEventPublisher;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.model.User;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
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
    @Mock
    UserRepository userRepository;
    @Mock
    TaskEventPublisher publisher;

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

    @Test
    void assignPublishesEventWhenAssigningSomeoneElse() {
        Task task = Task.builder().id(5L).title("Maquettes")
                .project(Project.builder().id(1L).build()).build();
        User bob = User.builder().id(2L).username("Bob").email("bob@test.com").build();

        when(taskRepository.findByIdAndProjectOwnerEmail(5L, "a@test.com"))
                .thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));

        taskService.assign(5L, 2L, "a@test.com");

        assertSame(bob, task.getAssignee());
        verify(publisher).publishAssigned(any(TaskAssignedEvent.class));
    }

    @Test
    void assignToSelfDoesNotPublishEvent() {
        Task task = Task.builder().id(5L).title("Maquettes")
                .project(Project.builder().id(1L).build()).build();
        User ahmed = User.builder().id(1L).email("a@test.com").build();

        when(taskRepository.findByIdAndProjectOwnerEmail(5L, "a@test.com"))
                .thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(ahmed));

        taskService.assign(5L, 1L, "a@test.com");

        verify(publisher, never()).publishAssigned(any());
    }
}
