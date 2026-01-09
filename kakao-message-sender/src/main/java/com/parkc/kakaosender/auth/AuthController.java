package com.parkc.kakaosender.auth;

import com.parkc.kakaosender.user.entity.PortalUser;
import com.parkc.kakaosender.user.repo.PortalUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PortalUserRepository portalUserRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          PortalUserRepository portalUserRepository) {
        this.authenticationManager = authenticationManager;
        this.portalUserRepository = portalUserRepository;
    }

    /**
     * 로그인
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                );

        // 1) 인증 수행 (UserDetailsService + PasswordEncoder 사용)
        Authentication authentication =
                authenticationManager.authenticate(authRequest);

        // 2) SecurityContext에 인증 정보 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 3) 세션 생성 → JSESSIONID 쿠키 발급
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                "SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext()
        );

        return ResponseEntity.ok().build();
    }

    /**
     * 현재 로그인 사용자 정보
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();

        PortalUser user = portalUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        return ResponseEntity.ok(new MeResponse(user.getUsername(), user.getRole()));
    }

    /**
     * 로그아웃
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}