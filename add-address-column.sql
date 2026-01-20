-- 운영 환경: 주소 컬럼 추가 스크립트
-- 기존 테이블을 DROP하지 않고 ALTER TABLE로 컬럼 추가

USE springofmsc;

-- 주소 컬럼 추가 (기존 데이터에 영향을 주지 않도록 NULL 허용)
ALTER TABLE users 
ADD COLUMN address VARCHAR(200) COMMENT '주소' AFTER age;

-- 인덱스 추가 (주소로 검색이 필요한 경우)
-- ALTER TABLE users ADD INDEX idx_address (address);

-- 기존 데이터에 주소 업데이트 (선택사항)
-- UPDATE users SET address = '서울시 강남구' WHERE id = 1;
-- UPDATE users SET address = '서울시 서초구' WHERE id = 2;
-- UPDATE users SET address = '서울시 송파구' WHERE id = 3;
-- UPDATE users SET address = '서울시 마포구' WHERE id = 4;
-- UPDATE users SET address = '서울시 종로구' WHERE id = 5;

