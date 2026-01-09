/* =========================================================
 * File: V004__add_friend_sync_log.sql
 * Desc: Create friend_sync_run table to track manual Kakao friend
 *       synchronization executions and results
 * Author: ParkC
 * Created: 2026-01-10
 * ========================================================= */
CREATE TABLE friend_sync_run (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    portal_user_id      BIGINT UNSIGNED NOT NULL COMMENT 'FK: portal_user.id (누가 동기화 실행했는지)',
    started_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '동기화 시작 시각',
    finished_at         DATETIME(6)     NULL COMMENT '동기화 종료 시각',
    fetched_count       INT             NOT NULL DEFAULT 0 COMMENT '카카오 API에서 조회된 친구 수(총합)',
    upserted_count      INT             NOT NULL DEFAULT 0 COMMENT 'DB upsert 처리된 친구 수',
    deactivated_count   INT             NOT NULL DEFAULT 0 COMMENT '이번 동기화에서 누락되어 비활성 처리된 수',
    status              VARCHAR(20)     NOT NULL DEFAULT 'RUNNING' COMMENT '상태(RUNNING/SUCCESS/FAILED)',
    error_message       VARCHAR(1024)   NULL COMMENT '실패 시 에러 메시지(요약)',
    PRIMARY KEY (id),
    KEY idx_sync_user_time (portal_user_id, started_at),
    CONSTRAINT fk_sync_run_portal_user
        FOREIGN KEY (portal_user_id)
        REFERENCES portal_user(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='친구 동기화 실행 로그(수동 버튼 트리거)';