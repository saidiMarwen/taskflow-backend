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
                    LEFT JOIN p.owner o
                    LEFT JOIN p.tasks t
                    LEFT JOIN t.assignee a
                    WHERE o.email = :email OR a.email = :email
            """)
    List<Project> findAccessible(@Param("email") String email);

    @Query("""
                SELECT DISTINCT p FROM Project p
                    LEFT JOIN p.owner o
                    LEFT JOIN p.tasks t
                    LEFT JOIN t.assignee a
                    WHERE p.id = :id AND (o.email = :email OR a.email = :email)
            """)
    Optional<Project> findAccessibleById(@Param("id") Long id, @Param("email") String email);
}
