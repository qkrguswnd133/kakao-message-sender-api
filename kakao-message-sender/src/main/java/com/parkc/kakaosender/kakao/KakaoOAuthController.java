package com.parkc.kakaosender.kakao;

import com.parkc.kakaosender.kakao.dto.KakaoOAuthExchangeRequest;
import com.parkc.kakaosender.kakao.dto.KakaoOAuthExchangeResponse;
import com.parkc.kakaosender.kakao.dto.KakaoOAuthStartResponse;
import com.parkc.kakaosender.kakao.service.KakaoOAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kakao/oauth")
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    public KakaoOAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @GetMapping("/start")
    public ResponseEntity<KakaoOAuthStartResponse> start(Authentication authentication, HttpSession session) {
        String username = authentication.getName();
        String authorizeUrl = kakaoOAuthService.buildAuthorizeUrl(username, session);
        return ResponseEntity.ok(new KakaoOAuthStartResponse(authorizeUrl));
    }

    @PostMapping("/exchange")
    public ResponseEntity<KakaoOAuthExchangeResponse> exchange(
            Authentication authentication,
            HttpSession session,
            @Valid @RequestBody KakaoOAuthExchangeRequest request
    ) {
        String username = authentication.getName();
        kakaoOAuthService.exchangeAndSave(username, request.code(), request.state(), session);
        return ResponseEntity.ok(new KakaoOAuthExchangeResponse(true));
    }
}