-- 운영 환경: 파일 첨부 관련 컬럼 추가 스크립트
-- 기존 테이블을 DROP하지 않고 ALTER TABLE로 컬럼 추가

USE msc_db;

-- 파일 관련 컬럼 추가 (기존 데이터에 영향을 주지 않도록 NULL 허용)
ALTER TABLE users 
ADD COLUMN file_name VARCHAR(255) COMMENT '원본 파일명' AFTER address,
ADD COLUMN file_path VARCHAR(500) COMMENT '파일 저장 경로' AFTER file_name,
ADD COLUMN file_size BIGINT COMMENT '파일 크기 (bytes)' AFTER file_path,
ADD COLUMN file_type VARCHAR(100) COMMENT '파일 타입 (MIME type)' AFTER file_size;

-- 인덱스 추가 (파일명으로 검색이 필요한 경우)
-- ALTER TABLE users ADD INDEX idx_file_name (file_name);
