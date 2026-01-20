-- 사용자 30명 추가 스크립트
-- 기존 데이터는 유지하고 30명을 추가합니다.

USE msc_db;

INSERT INTO users (user_id, name, email, phone, age, address) VALUES
('USER006', '정수진', 'jung.sujin@example.com', '010-1111-2222', 29, '서울시 마포구'),
('USER007', '강민호', 'kang.minho@example.com', '010-2222-3333', 31, '서울시 송파구'),
('USER008', '윤서연', 'yoon.seoyeon@example.com', '010-3333-4444', 26, '서울시 강동구'),
('USER009', '조현우', 'cho.hyunwoo@example.com', '010-4444-5555', 33, '서울시 영등포구'),
('USER010', '임지은', 'lim.jieun@example.com', '010-5555-6666', 28, '서울시 노원구'),
('USER011', '한동욱', 'han.dongwook@example.com', '010-6666-7777', 35, '서울시 은평구'),
('USER012', '오수빈', 'oh.soobin@example.com', '010-7777-8888', 24, '서울시 양천구'),
('USER013', '신태영', 'shin.taeyoung@example.com', '010-8888-9999', 30, '서울시 구로구'),
('USER014', '배지훈', 'bae.jihun@example.com', '010-9999-0000', 27, '서울시 금천구'),
('USER015', '류하늘', 'ryu.haneul@example.com', '010-0000-1111', 25, '서울시 관악구'),
('USER016', '송민지', 'song.minji@example.com', '010-1234-0001', 29, '서울시 강북구'),
('USER017', '권도현', 'kwon.dohyun@example.com', '010-1234-0002', 32, '서울시 성북구'),
('USER018', '황예린', 'hwang.yerin@example.com', '010-1234-0003', 26, '서울시 동대문구'),
('USER019', '고승현', 'go.seunghyun@example.com', '010-1234-0004', 34, '서울시 중랑구'),
('USER020', '문채원', 'moon.chaewon@example.com', '010-1234-0005', 28, '서울시 성동구'),
('USER021', '남준호', 'nam.junho@example.com', '010-1234-0006', 31, '서울시 광진구'),
('USER022', '도예나', 'do.yena@example.com', '010-1234-0007', 25, '서울시 용산구'),
('USER023', '라민수', 'ra.minsu@example.com', '010-1234-0008', 30, '서울시 중구'),
('USER024', '마지혜', 'ma.jihye@example.com', '010-1234-0009', 27, '서울시 종로구'),
('USER025', '백준서', 'baek.junseo@example.com', '010-1234-0010', 29, '서울시 서대문구'),
('USER026', '사나래', 'sa.narae@example.com', '010-1234-0011', 26, '서울시 마포구'),
('USER027', '아진우', 'ah.jinwoo@example.com', '010-1234-0012', 33, '서울시 강남구'),
('USER028', '자은서', 'ja.eunseo@example.com', '010-1234-0013', 28, '서울시 서초구'),
('USER029', '차민규', 'cha.mingyu@example.com', '010-1234-0014', 31, '서울시 송파구'),
('USER030', '카리나', 'ka.rina@example.com', '010-1234-0015', 24, '서울시 강동구'),
('USER031', '타현우', 'ta.hyunwoo@example.com', '010-1234-0016', 30, '서울시 영등포구'),
('USER032', '파서진', 'pa.seojin@example.com', '010-1234-0017', 27, '서울시 노원구'),
('USER033', '하준영', 'ha.junyoung@example.com', '010-1234-0018', 32, '서울시 은평구'),
('USER034', '허수아', 'heo.sua@example.com', '010-1234-0019', 25, '서울시 양천구'),
('USER035', '호민재', 'ho.minjae@example.com', '010-1234-0020', 29, '서울시 구로구');

-- 확인: 총 사용자 수 확인
SELECT COUNT(*) as total_users FROM users;
