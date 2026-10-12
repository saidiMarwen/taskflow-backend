package com.taskflow.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

    public static final String TOPIC = "task-events";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishAssigned(TaskAssignedEvent event){
        try{
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, String.valueOf(event.assigneeId()), json);
            log.info("Evenement publié : {}", json);
        }catch(Exception e) {
            log.error("Echec de publication kafka", e);
        }
    }
}
