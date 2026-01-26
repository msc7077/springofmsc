package com.example.springofmsc.domain.notice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AgencyNotice 엔티티 클래스
 * 
 * [엔티티란?]
 * - 데이터베이스 agency_notice 테이블과 1:1로 매핑되는 Java 클래스입니다.
 * - JPA가 이 클래스를 보고 데이터베이스와 소통합니다.
 * 
 * [Lombok 어노테이션]
 * - @Getter: 모든 필드의 getter 메서드 자동 생성
 * - @Setter: 모든 필드의 setter 메서드 자동 생성
 * - @NoArgsConstructor: 파라미터 없는 기본 생성자 자동 생성 (JPA 필수)
 */
@Entity
@Table(name = "agency_notice", schema = "msc_database")
@Getter
@Setter
@NoArgsConstructor
public class AgencyNotice {

	/**
	 * Primary Key
	 * - int NOT NULL AUTO_INCREMENT
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	/**
	 * MongoDB _id 참조
	 */
	@Column(name = "obj_id", length = 24)
	private String objId;

	/**
	 * 기관 ID
	 * - 프론트에서 받는 값으로 이걸로 조회할 예정
	 */
	@Column(name = "agency_id")
	private Integer agencyId;

	/**
	 * 사업자번호
	 * - NOT NULL
	 */
	@Column(name = "business_number", nullable = false, length = 20)
	private String businessNumber;

	/**
	 * 활동 제목
	 * - NOT NULL
	 */
	@Column(name = "subject", nullable = false, length = 255)
	private String subject;

	/**
	 * 활동 내용
	 * - TEXT 타입
	 */
	@Lob
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	/**
	 * 작성자
	 */
	@Column(name = "writer", length = 100)
	private String writer;

	/**
	 * 대상 공개 여부
	 * - N: 전체, C: 특정반공개, M: 특정멤버공개, S: 직원만공개
	 */
	@Column(name = "is_private", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
	private String isPrivate;

	/**
	 * 생성일시
	 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	/**
	 * 수정일시
	 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Lombok이 자동으로 Getter와 Setter 메서드를 생성합니다.
	// @Getter와 @Setter 어노테이션으로 모든 필드의 getter/setter가 자동 생성됩니다.
}
