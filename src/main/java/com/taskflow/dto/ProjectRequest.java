package com.taskflow.dto;


import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
            @NotBlank(message = "Le nom est obligatoire") String name,
            String description
    ) {}

