package com.parkc.kakaosender.kakao.service;

import com.parkc.kakaosender.kakao.dto.KakaoTokenResponse;
import com.parkc.kakaosender.kakao.entity.KakaoOAuthToken;
import com.parkc.kakaosender.kakao.repo.KakaoOAuthTokenRepository;
import com.parkc.kakaosender.user.entity.PortalUser;
import com.parkc.kakaosender.user.repo.PortalUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class KakaoOAuthService {

    private static final String SESSION_OAUTH_STATE = "KAKAO_OAUTH_STATE";

    private final RestClient restClient;
    private final PortalUserRepository portalUserRepository;
    private final KakaoOAuthTokenRepository tokenRepository;

    @Value("${kakao.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.oauth.authorize-url}")
    private String authorizeUrlBase;

    @Value("${kakao.oauth.token-url}")
    private String tokenUrl;

    public KakaoOAuthService(RestClient restClient,
                             PortalUserRepository portalUserRepository,
                             KakaoOAuthTokenRepository tokenRepository) {
        this.restClient = restClient;
        this.portalUserRepository = portalUserRepository;
        this.tokenRepository = tokenRepository;
    }

    /** ✅ 로그인한 유저의 portal_user.api_key로 authorize URL 생성 */
    public String buildAuthorizeUrl(String username, HttpSession session) {
        PortalUser user = portalUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        String clientId = normalizeClientId(user.getApiKey());

        String state = generateState();
        session.setAttribute(SESSION_OAUTH_STATE, state);

        return UriComponentsBuilder.fromHttpUrl(authorizeUrlBase)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .build(true)
                .toUriString();
    }

    /** ✅ state 검증 + code→token 교환 + DB 저장 (client_id는 유저 api_key) */
    @Transactional
    public void exchangeAndSave(String username, String code, String stateFromClient, HttpSession session) {
        validateState(stateFromClient, session);

        PortalUser user = portalUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        String clientId = normalizeClientId(user.getApiKey());
        String clientSecret = user.getClientSecret(); // nullable

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", code);

        // client_secret이 "있고 비어있지 않을 때만" 추가
        if (clientSecret != null && !clientSecret.trim().isEmpty()) {
            builder.queryParam("client_secret", clientSecret.trim());
        }

        String form = builder.build().getQuery();

        KakaoTokenResponse token = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(KakaoTokenResponse.class);

        if (token == null || token.accessToken() == null || token.refreshToken() == null) {
            throw new IllegalStateException("Kakao token response is empty");
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(token.expiresIn());

        KakaoOAuthToken entity = tokenRepository.findByPortalUserId(user.getId())
                .orElseGet(() -> new KakaoOAuthToken(user.getId(), token.accessToken(), token.refreshToken(), expiresAt));

        entity.setAccessToken(token.accessToken());
        entity.setRefreshToken(token.refreshToken());
        entity.setExpiresAt(expiresAt);
        entity.setScope(token.scope());

        tokenRepository.save(entity);

        // 1회용 state 소거
        session.removeAttribute(SESSION_OAUTH_STATE);
    }

    private void validateState(String stateFromClient, HttpSession session) {
        Object saved = session.getAttribute(SESSION_OAUTH_STATE);
        String expected = (saved instanceof String s) ? s : null;

        if (expected == null || stateFromClient == null || !expected.equals(stateFromClient)) {
            throw new IllegalStateException("Invalid OAuth state");
        }
    }

    private String normalizeClientId(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Kakao REST API Key(api_key) is not registered for this user");
        }
        return apiKey.trim();
    }

    private String generateState() {
        byte[] b = new byte[24];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}