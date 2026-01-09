package com.parkc.kakaosender.kakao.repo;

import com.parkc.kakaosender.kakao.entity.KakaoOAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface KakaoOAuthTokenRepository extends JpaRepository<KakaoOAuthToken, Long> {

    Optional<KakaoOAuthToken> findByPortalUserId(Long portalUserId);

    boolean existsByPortalUserId(Long portalUserId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    int deleteByPortalUserId(Long portalUserId); // ✅ void 대신 int 추천 (삭제 건수)
}