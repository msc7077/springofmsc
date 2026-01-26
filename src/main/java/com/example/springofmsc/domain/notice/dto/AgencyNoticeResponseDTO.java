package com.example.springofmsc.domain.notice.dto;

import java.time.LocalDateTime;

import com.example.springofmsc.domain.notice.entity.AgencyNotice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AgencyNotice Response DTO
 * 
 * [DTO란?]
 * - Data Transfer Object (데이터 전송 객체)
 * - Controller와 프론트엔드 간 데이터를 전송하기 위한 객체입니다.
 * - Entity와 분리하여 API 응답 형식을 독립적으로 관리합니다.
 * 
 * [왜 DTO를 사용하나?]
 * 1. 보안: 내부 필드(objId 등)를 숨길 수 있습니다.
 * 2. 유연성: 프론트엔드에 필요한 필드만 노출할 수 있습니다.
 * 3. 독립성: Entity 구조 변경이 API 응답에 영향을 주지 않습니다.
 * 4. 명확성: API 응답 형식이 명확해집니다.
 * 
 * [Lombok 어노테이션]
 * - @Getter, @Setter: getter/setter 자동 생성
 * - @NoArgsConstructor: 기본 생성자 자동 생성
 */
@Getter
@Setter
@NoArgsConstructor
public class AgencyNoticeResponseDTO {

	/**
	 * 공지사항 ID
	 */
	private Integer id;

	/**
	 * 기관 ID
	 */
	private Integer agencyId;

	/**
	 * 사업자번호
	 */
	private String businessNumber;

	/**
	 * 활동 제목
	 */
	private String subject;

	/**
	 * 활동 내용
	 */
	private String content;

	/**
	 * 작성자
	 */
	private String writer;

	/**
	 * 대상 공개 여부
	 * - N: 전체, C: 특정반공개, M: 특정멤버공개, S: 직원만공개
	 */
	private String isPrivate;

	/**
	 * 생성일시
	 */
	private LocalDateTime createdAt;

	/**
	 * 수정일시
	 */
	private LocalDateTime updatedAt;

	/**
	 * Entity를 DTO로 변환하는 생성자
	 * 
	 * [왜 생성자를 사용하나?]
	 * - Entity의 모든 필드를 DTO로 복사합니다.
	 * - objId 같은 내부 필드는 제외할 수 있습니다.
	 * 
	 * @param entity AgencyNotice 엔티티
	 */
	public AgencyNoticeResponseDTO(AgencyNotice entity) {
		this.id = entity.getId();
		this.agencyId = entity.getAgencyId();
		this.businessNumber = entity.getBusinessNumber();
		this.subject = entity.getSubject();
		this.content = entity.getContent();
		this.writer = entity.getWriter();
		this.isPrivate = entity.getIsPrivate();
		this.createdAt = entity.getCreatedAt();
		this.updatedAt = entity.getUpdatedAt();
		// objId는 내부 필드이므로 DTO에 포함하지 않음
	}
}
