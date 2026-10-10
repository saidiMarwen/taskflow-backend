package com.taskflow.repository;

import com.taskflow.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerEmail(String email);
    Optional<Project> findByIdAndOwnerEmail(Long id, String email);
}
