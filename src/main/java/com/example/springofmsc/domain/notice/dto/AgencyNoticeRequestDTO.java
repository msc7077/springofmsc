package com.example.springofmsc.domain.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AgencyNotice Request DTO
 * 
 * [DTO란?]
 * - Data Transfer Object (데이터 전송 객체)
 * - 프론트엔드에서 받는 요청 데이터를 담는 객체입니다.
 * 
 * [왜 DTO를 사용하나?]
 * 1. 검증: @Valid 어노테이션으로 입력값 검증 가능
 * 2. 명확성: 어떤 파라미터를 받는지 명확하게 정의
 * 3. 확장성: 나중에 파라미터 추가가 쉬움
 * 4. 페이징: 페이징 파라미터를 함께 관리
 * 
 * [페이징 파라미터]
 * - page: 페이지 번호 (0부터 시작, 기본값: 0)
 * - size: 페이지 크기 (한 페이지에 보여줄 개수, 기본값: 10)
 * 
 * [Lombok 어노테이션]
 * - @Getter, @Setter: getter/setter 자동 생성
 * - @NoArgsConstructor: 기본 생성자 자동 생성
 */
@Getter
@Setter
@NoArgsConstructor
public class AgencyNoticeRequestDTO {

	/**
	 * 기관 ID (필수)
	 * - 프론트에서 받는 값
	 */
	private Integer agencyId;

	/**
	 * 페이지 번호 (선택, 기본값: 0)
	 * - 0부터 시작합니다.
	 * - 예: 0 = 첫 번째 페이지, 1 = 두 번째 페이지
	 */
	private Integer page = 0;

	/**
	 * 페이지 크기 (선택, 기본값: 10)
	 * - 한 페이지에 보여줄 공지사항 개수
	 * - 예: 10 = 한 페이지에 10개씩 표시
	 */
	private Integer size = 10;
}
