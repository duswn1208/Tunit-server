-- 레슨 예약 시간 겹침 방지 (일회성 적용)
-- 운영 DB(Neon Postgres) / 로컬 Postgres 모두에서 한 번 실행하면 됩니다.
-- ReservationStatus 의 VALID_LESSON_STATUSES (REQUESTED / ACTIVE / COMPLETED) 와 동기화된 부분 인덱스.

-- 1. 범위 연산자(&&) 와 동등 비교(=) 를 GiST 인덱스에서 함께 쓰기 위한 확장
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- 2. 같은 튜터, 같은 날짜에서 활성 상태 예약끼리 시간이 겹치지 않도록 강제
ALTER TABLE lesson_reservation
    ADD CONSTRAINT no_overlap_lesson_reservation
    EXCLUDE USING GIST (
        tutor_profile_no WITH =,
        date             WITH =,
        tsrange(
            (date + start_time)::timestamp,
            (date + end_time)::timestamp,
            '[)'
        ) WITH &&
    ) WHERE (status IN ('REQUESTED', 'ACTIVE', 'COMPLETED'));
