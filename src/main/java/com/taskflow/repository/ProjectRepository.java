package com.taskflow.repository;

import com.taskflow.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerEmail(String email);
    Optional<Project> findByIdAndOwnerEmail(Long id, String email);

    @Query("""
    SELECT DISTINCT p FROM Project p
    LEFT JOIN p.tasks t
    WHERE p.owner.email = :email OR t.assignee.email = :email
""")
    List<Project> findAccessible(@Param("email") String email);

    @Query("""
    SELECT DISTINCT p FROM Project p
    LEFT JOIN p.tasks t
    WHERE p.id = :id AND (p.owner.email = :email OR t.assignee.email = :email)
""")
    Optional<Project> findAccessibleById(@Param("id") Long id, @Param("email") String email);
}
