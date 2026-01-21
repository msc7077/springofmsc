package com.example.springofmsc.domain.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.springofmsc.domain.user.entity.User;

/**
 * 사용자 조회 응답 DTO
 * 실제 DB 스키마에 맞춘 구조
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String userid; // 로그인 아이디
    private String name; // 사용자 이름
    private String userType;
    private User.AccountType accountType; // 회원 유형
    private String userci; // 본인인증 CI
    private String userdi; // 본인인증 DI
    private String businessNumber; // 사업자 번호
    private User.YesNo isAdmin; // 내부 관리자 여부
    private String status; // 계정 상태 (A:활성화, D:탈퇴, R:휴면, H:보류)
    private LocalDateTime statusAt; // 계정 상태 마지막 수정일
    private User.YesNo expired; // 탈퇴여부
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
