package com.parkc.kakaosender.kakao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "kakao_oauth_token",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_kakao_token_user", columnNames = {"portal_user_id"})
        },
        indexes = {
                @Index(name = "idx_kakao_user_id", columnList = "kakao_user_id")
        }
)
public class KakaoOAuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "portal_user_id", nullable = false, updatable = false)
    private Long portalUserId;

    @Column(name = "kakao_user_id")
    private Long kakaoUserId;

    @Column(name = "access_token", nullable = false, length = 1024)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false, length = 1024)
    private String refreshToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "scope", length = 255)
    private String scope;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected KakaoOAuthToken() {
    }

    public KakaoOAuthToken(Long portalUserId, String accessToken, String refreshToken, LocalDateTime expiresAt) {
        this.portalUserId = portalUserId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}