package com.parkc.kakaosender.kakao;

import com.parkc.kakaosender.kakao.service.KakaoConnectionService;
import com.parkc.kakaosender.user.entity.PortalUser;
import com.parkc.kakaosender.user.repo.PortalUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/kakao")
public class KakaoConnectionController {

    private final KakaoConnectionService kakaoConnectionService;
    private final PortalUserRepository portalUserRepository;

    public KakaoConnectionController(
            KakaoConnectionService kakaoConnectionService,
            PortalUserRepository portalUserRepository
    ) {
        this.kakaoConnectionService = kakaoConnectionService;
        this.portalUserRepository = portalUserRepository;
    }

    @PostMapping("/unlink")
    public ResponseEntity<Void> unlink(Authentication authentication) {
        PortalUser user = portalUserRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        kakaoConnectionService.unlink(user.getId());

        return ResponseEntity.noContent().build();
    }
}