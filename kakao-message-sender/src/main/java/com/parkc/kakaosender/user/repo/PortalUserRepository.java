package com.parkc.kakaosender.user.repo;

import com.parkc.kakaosender.user.entity.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {

    Optional<PortalUser> findByUsername(String username);

    boolean existsByUsername(String username);
}