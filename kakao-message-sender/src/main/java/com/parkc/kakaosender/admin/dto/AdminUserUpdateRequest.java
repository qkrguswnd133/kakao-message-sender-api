package com.parkc.kakaosender.admin.dto;

public record AdminUserUpdateRequest(
        String role,        // nullable
        Boolean enabled,    // nullable
        String apiKey,       // nullable (null이면 변경 안함, ""이면 비우기 처리 가능)
        String clientSecret    // nullable (null = no change, "" = clear)
) {}