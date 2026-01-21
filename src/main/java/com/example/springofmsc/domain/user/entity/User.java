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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 계정 정보 엔티티
 * 실제 DB 스키마에 맞춘 구조
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "userid", nullable = false, unique = true, length = 100)
    private String userid; // 로그인 아이디

    @Column(name = "name", length = 100)
    private String name; // 사용자 이름

    @Column(name = "user_type", length = 50)
    private String userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", columnDefinition = "ENUM('AGENCY','GENERATED','PERSONAL','PERSONAL_AGENCY_MEMBER','PERSONAL_GUARDIAN')")
    private AccountType accountType; // 회원 유형

    @Column(name = "userci", length = 255)
    private String userci; // 본인인증 CI

    @Column(name = "userdi", length = 255)
    private String userdi; // 본인인증 DI

    @Column(name = "business_number", length = 100)
    private String businessNumber; // 사업자 번호

    @Enumerated(EnumType.STRING)
    @Column(name = "is_admin", nullable = false, columnDefinition = "ENUM('Y','N') DEFAULT 'N'")
    private YesNo isAdmin = YesNo.N; // 내부 관리자 여부

    @Column(name = "status", nullable = false, length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'A'")
    private String status = "A"; // 계정 상태 (A:활성화, D:탈퇴, R:휴면, H:보류)

    @Column(name = "status_at")
    private LocalDateTime statusAt; // 계정 상태 마지막 수정일

    @Enumerated(EnumType.STRING)
    @Column(name = "expired", nullable = false, columnDefinition = "ENUM('Y','N') DEFAULT 'N'")
    private YesNo expired = YesNo.N; // 탈퇴여부

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    /**
     * 회원 유형 enum
     */
    public enum AccountType {
        AGENCY, // 기관
        GENERATED, // 생성된 계정
        PERSONAL, // 개인
        PERSONAL_AGENCY_MEMBER, // 개인 기관 멤버
        PERSONAL_GUARDIAN // 개인 보호자
    }

    /**
     * Y/N enum
     */
    public enum YesNo {
        Y, N
    }
}
