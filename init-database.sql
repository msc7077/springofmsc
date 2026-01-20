-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS springofmsc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 사용
USE springofmsc;

-- 기존 테이블이 있다면 삭제 (주의: 데이터가 모두 삭제됩니다)
DROP TABLE IF EXISTS users;

-- users 테이블 생성 (id는 AUTO_INCREMENT로 자동 생성됨)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '자동 증가 ID',
    user_id VARCHAR(50) NOT NULL UNIQUE COMMENT '사용자 고유 ID',
    name VARCHAR(50) NOT NULL COMMENT '이름',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일',
    phone VARCHAR(20) COMMENT '전화번호',
    age INT COMMENT '나이',
    INDEX idx_email (email),
    INDEX idx_name (name),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 테이블';

-- 샘플 데이터 삽입 (선택사항)
INSERT INTO users (user_id, name, email, phone, address, age) VALUES
('USER001', '홍길동', 'hong@example.com', '010-1234-5678', '서울시 강남구', 30),
('USER002', '김철수', 'kim@example.com', '010-2345-6789', '서울시 서초구', 25),
('USER003', '이영희', 'lee@example.com', '010-3456-7890', '서울시 종로구', 28),
('USER004', '박민수', 'park@example.com', '010-4567-8901', '서울시 강서구', 32),
('USER005', '최지영', 'choi@example.com', '010-5678-9012', '서울시 도봉구', 27);

-- 주소 컬럼 추가 (기존 데이터에 영향을 주지 않도록 NULL 허용)
-- 서비스 중단 없음: ALTER TABLE은 테이블 락을 걸지만, 빠르게 완료됩니다
ALTER TABLE users 
ADD COLUMN address VARCHAR(200) COMMENT '주소' AFTER age;

UPDATE users SET address = '서울시 강남구' WHERE id = 1;
UPDATE users SET address = '서울시 서초구' WHERE id = 2;
-- ...