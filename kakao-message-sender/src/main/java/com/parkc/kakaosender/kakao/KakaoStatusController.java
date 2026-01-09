package com.parkc.kakaosender.kakao;

import com.parkc.kakaosender.kakao.repo.KakaoOAuthTokenRepository;
import com.parkc.kakaosender.user.entity.PortalUser;
import com.parkc.kakaosender.user.repo.PortalUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kakao")
public class KakaoStatusController {

    private final PortalUserRepository portalUserRepository;
    private final KakaoOAuthTokenRepository tokenRepository;

    public KakaoStatusController(
            PortalUserRepository portalUserRepository,
            KakaoOAuthTokenRepository tokenRepository
    ) {
        this.portalUserRepository = portalUserRepository;
        this.tokenRepository = tokenRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<KakaoStatusResponse> status(Authentication authentication) {
        String username = authentication.getName();

        PortalUser user = portalUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB: " + username));

        boolean linked = tokenRepository.existsByPortalUserId(user.getId());

        return ResponseEntity.ok(new KakaoStatusResponse(linked));
    }

    public record KakaoStatusResponse(boolean linked) {}
}