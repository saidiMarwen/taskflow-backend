package com.taskflow.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Le nom d'utilisateur est obligatoire") String username,
        @NotBlank @Email(message = "Email invalide") String email,
        @NotBlank @Size(min = 6, message = "6 caractères minimum") String password
) {}