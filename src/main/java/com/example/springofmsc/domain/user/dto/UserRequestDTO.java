package com.example.springofmsc.domain.user.dto;

import com.example.springofmsc.domain.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 생성/수정 요청 DTO
 * 실제 DB 스키마에 맞춘 구조
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "userid는 필수입니다")
    @Size(min = 3, max = 100, message = "userid는 3자 이상 100자 이하여야 합니다")
    private String userid; // 로그인 아이디

    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    private String name; // 사용자 이름

    @Size(max = 50, message = "user_type은 50자 이하여야 합니다")
    private String userType;

    private User.AccountType accountType; // 회원 유형

    @Size(max = 255, message = "userci는 255자 이하여야 합니다")
    private String userci; // 본인인증 CI

    @Size(max = 255, message = "userdi는 255자 이하여야 합니다")
    private String userdi; // 본인인증 DI

    @Size(max = 100, message = "사업자 번호는 100자 이하여야 합니다")
    private String businessNumber; // 사업자 번호

    private User.YesNo isAdmin; // 내부 관리자 여부

    @Size(max = 10, message = "status는 10자 이하여야 합니다")
    private String status; // 계정 상태 (A:활성화, D:탈퇴, R:휴면, H:보류)

    private User.YesNo expired; // 탈퇴여부
}
