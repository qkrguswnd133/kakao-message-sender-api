package com.parkc.kakaosender.admin.dto;

import java.time.LocalDateTime;

public record AdminUserResponse(
        Long id,
        String username,
        String role,
        boolean enabled,
        String apiKey,
        String clientSecret,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}