package com.taskflow.service;

import com.taskflow.dto.ProjectRequest;
import com.taskflow.dto.ProjectResponse;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Project;
import com.taskflow.model.User;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

//    @Transactional(readOnly = true)
//    public List<ProjectResponse> findAll(String email) {
//        return projectRepository.findByOwnerEmail(email).stream().map(this::toResponse).toList();
//    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAccessible(String email) {
        return projectRepository.findAccessible(email).stream().map(this::toResponse).toList();
    }

//    @Transactional(readOnly = true)
//    public ProjectResponse findById(Long id, String email) {
//        return toResponse(getEntity(id, email));
//    }

    @Transactional(readOnly = true)
    public ProjectResponse findAccessibleById(Long id, String email) {
        return toResponse(getAccessibleEntity(id, email));
    }

    public ProjectResponse create(ProjectRequest request, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .owner(owner)
                .build();
        return toResponse(projectRepository.save(project));
    }

    public ProjectResponse update(Long id, ProjectRequest request, String email) {
        Project project = getEntity(id, email);
        project.setName(request.name());
        project.setDescription(request.description());
        return toResponse(project);
    }

    public void delete(Long id, String email) {
        projectRepository.delete(getEntity(id, email));
    }

    /** Ne retourne le projet que s'il appartient à l'utilisateur, sinon 404. */
    public Project getEntity(Long id, String email) {
        return projectRepository.findByIdAndOwnerEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable : " + id));
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(),
                p.getTasks().size(), p.getCreatedAt());
    }

    /** Lecture : propriétaire OU assigné à une tâche du projet. */
    public Project getAccessibleEntity(Long id, String email) {
        return projectRepository.findAccessibleById(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable : " + id));
    }
}