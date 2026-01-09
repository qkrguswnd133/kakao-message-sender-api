/* =========================================================
 * File: V002__add_kakao_oauth.sql
 * Desc: Add kakao_oauth_token table to store Kakao OAuth access
 *       and refresh tokens linked to portal users
 * Author: ParkC
 * Created: 2026-01-10
 * ========================================================= */
CREATE TABLE kakao_oauth_token (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    portal_user_id  BIGINT UNSIGNED NOT NULL COMMENT 'FK: portal_user.id (이 서비스 사용자)',
    kakao_user_id   BIGINT          NULL COMMENT '카카오 사용자 ID(가능하면 저장; /v2/user/me로 조회)',
    access_token    VARCHAR(1024)   NOT NULL COMMENT '카카오 access_token',
    refresh_token   VARCHAR(1024)   NOT NULL COMMENT '카카오 refresh_token',
    expires_at      DATETIME(6)     NOT NULL COMMENT 'access_token 만료 시각(서버가 계산해서 저장)',
    scope           VARCHAR(255)    NULL COMMENT '동의 스코프(예: friends,talk_message 등)',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시각',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                        ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시각',
    PRIMARY KEY (id),
    UNIQUE KEY uk_kakao_token_user (portal_user_id),
    KEY idx_kakao_user_id (kakao_user_id),
    CONSTRAINT fk_kakao_token_portal_user
        FOREIGN KEY (portal_user_id)
        REFERENCES portal_user(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='카카오 OAuth 토큰(서비스 사용자별 1개)';