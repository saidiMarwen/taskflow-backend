package com.taskflow.event;

public record TaskAssignedEvent(Long taskId, String taskTitle, Long projectId,
                                Long assigneeId, String assignedBy) {}