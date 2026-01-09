package com.parkc.kakaosender.kakao.dto;

import jakarta.validation.constraints.NotBlank;

public record KakaoOAuthExchangeRequest(
        @NotBlank String code,
        @NotBlank String state
) {}