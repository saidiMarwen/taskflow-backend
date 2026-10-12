package com.taskflow.event;

import com.taskflow.model.Notification;
import com.taskflow.repository.NotificationRepository;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = TaskEventPublisher.TOPIC)
    public void onTaskAssigned(String json) throws Exception {
        TaskAssignedEvent event = objectMapper.readValue(json, TaskAssignedEvent.class);
        log.info("Événement reçu : {}", event);

        userRepository.findById(event.assigneeId()).ifPresent(user ->
                notificationRepository.save(Notification.builder()
                        .user(user)
                        .projectId(event.projectId())
                        .message(event.assignedBy() + " t'a assigné la tâche « " + event.taskTitle() + " »")
                        .build()));
    }
}
