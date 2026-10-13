package com.taskflow.repository;

import com.taskflow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    Optional<Task> findByIdAndProjectOwnerEmail(Long id, String email);

    @Query("""
                SELECT t FROM Task t
                    LEFT JOIN t.project p
                    LEFT JOIN p.owner o
                    LEFT JOIN t.assignee a
                    WHERE t.id = :id AND (o.email = :email OR a.email = :email)
            """)
    Optional<Task> findAccessibleById(@Param("id") Long id, @Param("email") String email);

}
