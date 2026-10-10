package com.taskflow.service;

import com.taskflow.dto.ProjectRequest;
import com.taskflow.dto.ProjectResponse;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Project;
import com.taskflow.model.User;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock ProjectRepository projectRepository;
    @Mock UserRepository userRepository;
    @InjectMocks ProjectService projectService;

    @Test
    void findAllReturnsOnlyProjectsOfTheUser() {
        Project p = Project.builder().id(1L).name("Site").build();
        when(projectRepository.findByOwnerEmail("a@test.com")).thenReturn(List.of(p));

        List<ProjectResponse> result = projectService.findAll("a@test.com");

        assertEquals(1, result.size());
        assertEquals("Site", result.get(0).name());
    }

    @Test
    void createAssignsTheOwner() {
        User owner = User.builder().id(1L).email("a@test.com").build();
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        projectService.create(new ProjectRequest("Site", "Refonte"), "a@test.com");

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        assertSame(owner, captor.getValue().getOwner());
        assertEquals("Site", captor.getValue().getName());
    }

    @Test
    void findByIdThrowsWhenProjectBelongsToSomeoneElse() {
        when(projectRepository.findByIdAndOwnerEmail(1L, "bob@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.findById(1L, "bob@test.com"));
    }

    @Test
    void deleteRemovesTheProject() {
        Project p = Project.builder().id(1L).build();
        when(projectRepository.findByIdAndOwnerEmail(1L, "a@test.com")).thenReturn(Optional.of(p));

        projectService.delete(1L, "a@test.com");

        verify(projectRepository).delete(p);
    }
}