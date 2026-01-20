-- 기존 테이블만 삭제하고 다시 생성하는 스크립트
-- (샘플 데이터는 삽입하지 않음)

USE springofmsc;

-- 기존 테이블 삭제 (주의: 데이터가 모두 삭제됩니다)
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

