package com.parkc.kakaosender.auth;

import com.parkc.kakaosender.user.entity.PortalUser;
import com.parkc.kakaosender.user.repo.PortalUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PortalUserDetailsService implements UserDetailsService {

    private final PortalUserRepository repo;

    public PortalUserDetailsService(PortalUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PortalUser u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        boolean enabled = Boolean.TRUE.equals(u.getEnabled());

        // Spring Security의 "roles"는 내부적으로 ROLE_ prefix를 붙임
        // DB에는 ADMIN/USER 등으로 저장하고, 여기서 roles(u.getRole())로 매핑
        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .disabled(!enabled)
                .roles(u.getRole())   // e.g. "ADMIN" or "USER"
                .build();
    }
}