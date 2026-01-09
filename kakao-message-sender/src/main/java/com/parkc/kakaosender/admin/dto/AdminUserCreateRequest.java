package com.parkc.kakaosender.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminUserCreateRequest(
        @NotBlank @Size(min = 2, max = 50) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank String role,              // "ADMIN" or "USER"
        Boolean enabled,
        String apiKey,                       // Kakao REST API Key (optional)
        String clientSecret
) {}