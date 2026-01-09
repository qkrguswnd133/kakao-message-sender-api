package com.parkc.kakaosender.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "portal_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_portal_user_username", columnNames = {"username"})
        },
        indexes = {
                @Index(name = "idx_portal_user_enabled", columnList = "enabled"),
                @Index(name = "idx_portal_user_role", columnList = "role")
        }
)
public class PortalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "api_key", columnDefinition = "char(40)")
    private String apiKey;

    @Column(name = "client_secret", length = 128)
    private String clientSecret;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected PortalUser() {
        // JPA only
    }

    public PortalUser(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : "USER";
        this.enabled = true;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.enabled == null) this.enabled = true;
        if (this.role == null || this.role.isBlank()) this.role = "USER";
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}