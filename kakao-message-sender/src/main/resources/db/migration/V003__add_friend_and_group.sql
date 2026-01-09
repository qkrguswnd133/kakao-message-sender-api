/* =========================================================
 * File: V003__add_friend_and_group.sql
 * Desc: Create Kakao friend master tables and recipient group
 *       management tables for message targeting
 * Author: ParkC
 * Created: 2026-01-10
 * ========================================================= */
CREATE TABLE kakao_friend (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    portal_user_id  BIGINT UNSIGNED NOT NULL COMMENT 'FK: portal_user.id (누구의 카카오 친구 목록인지)',
    uuid            VARCHAR(80)     NOT NULL COMMENT '카카오 친구 uuid(메시지 발송 대상 식별자)',
    nickname        VARCHAR(100)    NULL COMMENT '친구 닉네임(동기화 시점 기준)',
    profile_thumb   VARCHAR(512)    NULL COMMENT '프로필 썸네일 URL(있으면 저장)',
    favorite        TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '즐겨찾기/핀(선택 기능)',
    is_active       TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '활성 여부(동기화에서 누락되면 0으로 처리 가능)',
    last_synced_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '마지막 동기화 시각',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시각',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                        ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시각',
    PRIMARY KEY (id),
    UNIQUE KEY uk_friend_user_uuid (portal_user_id, uuid),
    KEY idx_friend_user_active (portal_user_id, is_active),
    KEY idx_friend_user_nickname (portal_user_id, nickname),
    CONSTRAINT fk_friend_portal_user
        FOREIGN KEY (portal_user_id)
        REFERENCES portal_user(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='카카오 친구(수신자) 마스터(동기화 적재)';


CREATE TABLE recipient_group (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    portal_user_id  BIGINT UNSIGNED NOT NULL COMMENT 'FK: portal_user.id (그룹 소유자)',
    name            VARCHAR(80)     NOT NULL COMMENT '그룹명(사용자 내에서 고유 권장)',
    description     VARCHAR(255)    NULL COMMENT '그룹 설명',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시각',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                        ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시각',
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_user_name (portal_user_id, name),
    KEY idx_group_user (portal_user_id),
    CONSTRAINT fk_group_portal_user
        FOREIGN KEY (portal_user_id)
        REFERENCES portal_user(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='수신자 그룹(서비스 사용자별)';


CREATE TABLE recipient_group_member (
    group_id        BIGINT UNSIGNED NOT NULL COMMENT 'FK: recipient_group.id',
    friend_id       BIGINT UNSIGNED NOT NULL COMMENT 'FK: kakao_friend.id',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '추가 시각',
    PRIMARY KEY (group_id, friend_id),
    KEY idx_member_friend (friend_id),
    CONSTRAINT fk_group_member_group
        FOREIGN KEY (group_id)
        REFERENCES recipient_group(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_group_member_friend
        FOREIGN KEY (friend_id)
        REFERENCES kakao_friend(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='그룹-친구 매핑(N:M)';