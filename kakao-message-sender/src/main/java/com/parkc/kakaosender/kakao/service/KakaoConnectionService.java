package com.parkc.kakaosender.kakao.service;

import com.parkc.kakaosender.kakao.repo.KakaoOAuthTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaoConnectionService {

    private final KakaoOAuthTokenRepository kakaoTokenRepo;

    public KakaoConnectionService(KakaoOAuthTokenRepository kakaoTokenRepo) {
        this.kakaoTokenRepo = kakaoTokenRepo;
    }

    @Transactional
    public boolean unlink(Long portalUserId) {
        return kakaoTokenRepo.deleteByPortalUserId(portalUserId) > 0;
    }
}