package com.taskflow.repository;

import com.taskflow.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);

    long countByUserEmailAndReadFalse(String email);

    Optional<Notification> findByIdAndUserEmail(Long id, String email);
}