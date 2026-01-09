package com.parkc.kakaosender.auth;

public record MeResponse(
        String username,
        String role
) {}