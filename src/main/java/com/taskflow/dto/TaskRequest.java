package com.taskflow.dto;

import com.taskflow.model.TaskPriority;
import com.taskflow.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Le titre est obligatoire") String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate
) {}