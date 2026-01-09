package com.parkc.kakaosender.admin.service;

import com.parkc.kakaosender.admin.dto.*;
import com.parkc.kakaosender.user.entity.PortalUser;
import com.parkc.kakaosender.user.repo.PortalUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.parkc.kakaosender.kakao.repo.KakaoOAuthTokenRepository;

import java.util.List;
import java.util.Objects;

@Service
public class AdminUserService {

    private final PortalUserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final KakaoOAuthTokenRepository tokenRepository;

    public AdminUserService(PortalUserRepository repo,
                            PasswordEncoder passwordEncoder,
                            KakaoOAuthTokenRepository tokenRepository) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public AdminUserResponse create(AdminUserCreateRequest req) {
        if (repo.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already exists: " + req.username());
        }

        PortalUser u = new PortalUser(
                req.username(),
                passwordEncoder.encode(req.password()),
                normalizeRole(req.role())
        );

        if (req.enabled() != null) u.setEnabled(req.enabled());
        if (req.apiKey() != null) u.setApiKey(normalizeApiKey(req.apiKey()));

        PortalUser saved = repo.save(u);
        return toResponse(saved);
    }

    @Transactional
    public AdminUserResponse update(Long id, AdminUserUpdateRequest req) {
        PortalUser u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        boolean apiKeyChanged = false;
        boolean clientSecretChanged = false;

        if (req.role() != null) {
            u.setRole(normalizeRole(req.role()));
        }
        if (req.enabled() != null) {
            u.setEnabled(req.enabled());
        }


        // api_key
        if (req.apiKey() != null) {
            String normalized = req.apiKey().trim();
            if (normalized.isEmpty()) normalized = null;

            if (!Objects.equals(u.getApiKey(), normalized)) {
                apiKeyChanged = true;
                u.setApiKey(normalized);
            }
        }

        // client_secret
        if (req.clientSecret() != null) {
            String normalized = req.clientSecret().trim();
            if (normalized.isEmpty()) normalized = null;

            if (!Objects.equals(u.getClientSecret(), normalized)) {
                clientSecretChanged = true;
                u.setClientSecret(normalized);
            }
        }

        //  키/시크릿 바뀌면 기존 카카오 토큰 무효화
        if (apiKeyChanged || clientSecretChanged) {
            tokenRepository.deleteByPortalUserId(u.getId());
        }

        return toResponse(u); // dirty checking
    }

    @Transactional
    public void resetPassword(Long id, AdminUserPasswordRequest req) {
        PortalUser u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
    }

    private AdminUserResponse toResponse(PortalUser u) {
        return new AdminUserResponse(
                u.getId(),
                u.getUsername(),
                u.getRole(),
                Boolean.TRUE.equals(u.getEnabled()),
                u.getApiKey(),
                u.getClientSecret(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }

    private String normalizeRole(String role) {
        String r = role == null ? "" : role.trim().toUpperCase();
        if (!r.equals("ADMIN") && !r.equals("USER")) {
            throw new IllegalArgumentException("Invalid role: " + role + " (allowed: ADMIN, USER)");
        }
        return r;
    }

    private String normalizeApiKey(String apiKey) {
        return apiKey.trim(); // 필요하면 길이/형식 검증 추가
    }
}