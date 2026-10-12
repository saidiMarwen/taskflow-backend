package com.taskflow.controller;

import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Notification;
import com.taskflow.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    public record NotificationResponse(Long id, String message, Long projectId,
                                       boolean read, LocalDateTime createdAt) {}

    private final NotificationRepository repository;

    @GetMapping
    public List<NotificationResponse> list(Authentication auth) {
        return repository.findByUserEmailOrderByCreatedAtDesc(auth.getName()).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(),
                        n.getProjectId(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Authentication auth) {
        return Map.of("count", repository.countByUserEmailAndReadFalse(auth.getName()));
    }

    @PatchMapping("/{id}/read")
    @Transactional
    public void markAsRead(@PathVariable Long id, Authentication auth) {
        Notification n = repository.findByIdAndUserEmail(id, auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable"));
        n.setRead(true);
    }
}
