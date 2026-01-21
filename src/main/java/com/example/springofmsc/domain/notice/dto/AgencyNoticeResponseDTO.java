package com.example.springofmsc.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공지사항 응답 DTO
 * Swagger에서 표시하기 위한 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AgencyNoticeResponseDTO {
    private Integer id; // DB 테이블이 INT 타입이므로 Integer 사용
    private String division; // 구분 (📢 전체, 💼 직원전용, 🌻 반별, 🔒 개별)
    private String subject; // 제목
    private Integer attachCount; // 첨부파일 수
    private Integer readCount; // 읽음 여부 (1: 읽음, 0: 안 읽음)
}
