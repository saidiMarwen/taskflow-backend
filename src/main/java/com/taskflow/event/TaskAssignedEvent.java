package com.taskflow.event;

public record TaskAssignedEvent(Long taskId, String taskTitle, Long projectId,
                                String assigneeEmail, String assignedBy) {}