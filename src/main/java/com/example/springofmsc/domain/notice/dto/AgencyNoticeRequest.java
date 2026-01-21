package com.example.springofmsc.domain.notice.dto;

/**
 * 공지사항 조회용 인터페이스 (Projection)
 * Repository의 네이티브 쿼리 결과를 매핑하기 위해 사용
 */
public interface AgencyNoticeRequest {
    Integer getId(); // DB 테이블이 INT 타입이므로 Integer 사용

    String getDivision(); // 구분 (전체, 직원전용 등 CASE문 결과)

    String getSubject(); // 제목

    Integer getAttachCount(); // 첨부파일 수

    Integer getReadCount(); // 읽음 여부 (1 or 0)
}