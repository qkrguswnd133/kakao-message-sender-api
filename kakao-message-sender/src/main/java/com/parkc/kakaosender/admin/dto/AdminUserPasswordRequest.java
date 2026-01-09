package com.parkc.kakaosender.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminUserPasswordRequest(
        @NotBlank @Size(min = 6, max = 100) String newPassword
) {}