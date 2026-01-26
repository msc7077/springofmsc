package com.example.springofmsc.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User 엔티티 클래스
 * 
 * [엔티티란?]
 * - 데이터베이스 users 테이블과 1:1로 매핑되는 Java 클래스입니다.
 * - JPA가 이 클래스를 보고 데이터베이스와 소통합니다.
 * 
 * [Lombok 어노테이션]
 * - @Getter: 모든 필드의 getter 메서드 자동 생성
 * - @Setter: 모든 필드의 setter 메서드 자동 생성
 * - @NoArgsConstructor: 파라미터 없는 기본 생성자 자동 생성 (JPA 필수)
 */
@Entity
@Table(name = "users", schema = "msc_database")
@Getter
@Setter
@NoArgsConstructor
public class User {

	/**
	 * 회원 ID (Primary Key)
	 * - bigint unsigned NOT NULL AUTO_INCREMENT
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 로그인 아이디
	 * - varchar(100) NOT NULL, UNIQUE
	 */
	@Column(name = "userid", nullable = false, unique = true, length = 100)
	private String userid;

	/**
	 * 사용자 이름
	 * - varchar(100) DEFAULT NULL
	 */
	@Column(name = "name", length = 100)
	private String name;

	/**
	 * 사용자 타입
	 */
	@Column(name = "user_type", length = 50)
	private String userType;

	/**
	 * 회원 유형
	 * -
	 * enum('AGENCY','GENERATED','PERSONAL','PERSONAL_AGENCY_MEMBER','PERSONAL_GUARDIAN')
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type", length = 50)
	private AccountType accountType;

	/**
	 * 본인인증 CI
	 */
	@Column(name = "userci", length = 255)
	private String userci;

	/**
	 * 본인인증 DI
	 */
	@Column(name = "userdi", length = 255)
	private String userdi;

	/**
	 * 사업자 번호
	 * - agency_notice 테이블의 business_number와 연결될 수 있음
	 */
	@Column(name = "business_number", length = 100)
	private String businessNumber;

	/**
	 * 내부 관리자 여부
	 * - enum('Y','N') NOT NULL DEFAULT 'N'
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "is_admin", nullable = false, length = 1, columnDefinition = "ENUM('Y','N') DEFAULT 'N'")
	private IsAdmin isAdmin;

	/**
	 * 계정 상태
	 * - varchar(10) NOT NULL DEFAULT 'A'
	 * - A:활성화, D:탈퇴된 상태, R:휴면계정, H:보류
	 */
	@Column(name = "status", nullable = false, length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'A'")
	private String status;

	/**
	 * 계정 상태 마지막 수정일
	 */
	@Column(name = "status_at")
	private LocalDateTime statusAt;

	/**
	 * 탈퇴여부
	 * - enum('Y','N') NOT NULL DEFAULT 'N'
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "expired", nullable = false, length = 1, columnDefinition = "ENUM('Y','N') DEFAULT 'N'")
	private Expired expired;

	/**
	 * 탈퇴일시
	 */
	@Column(name = "expired_at")
	private LocalDateTime expiredAt;

	/**
	 * 생성일시
	 * - timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * 수정일시
	 * - timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
	 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/**
	 * 회원 유형 enum
	 */
	public enum AccountType {
		AGENCY,
		GENERATED,
		PERSONAL,
		PERSONAL_AGENCY_MEMBER,
		PERSONAL_GUARDIAN
	}

	/**
	 * 관리자 여부 enum
	 */
	public enum IsAdmin {
		Y, N
	}

	/**
	 * 탈퇴 여부 enum
	 */
	public enum Expired {
		Y, N
	}
}
