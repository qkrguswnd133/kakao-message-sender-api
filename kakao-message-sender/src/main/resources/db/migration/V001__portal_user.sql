/* =========================================================
 * File: V001__portal_user.sql
 * Desc: Create portal_user table for service login and access control
 *       (ID/PASSWORD based authentication)
 * Author: ParkC
 * Created: 2026-01-10
 * ========================================================= */
CREATE TABLE portal_user (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    username        VARCHAR(50)     NOT NULL COMMENT '로그인 ID(고유)',
    password_hash   VARCHAR(255)    NOT NULL COMMENT '비밀번호 해시(BCrypt)',
    api_key         CHAR(40)        NULL COMMENT 'Kakao REST API Key (OAuth client_id)',
    enabled         TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '사용 가능 여부(0=로그인 차단)',
    role            VARCHAR(20)     NOT NULL DEFAULT 'USER' COMMENT '권한(예: USER/ADMIN)',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시각',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                        ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시각',
    PRIMARY KEY (id),
    UNIQUE KEY uk_portal_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='관리 콘솔 로그인 사용자';