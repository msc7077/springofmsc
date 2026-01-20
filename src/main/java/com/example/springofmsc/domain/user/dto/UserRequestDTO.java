package com.example.springofmsc.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 생성/수정 요청 DTO
 * 클라이언트로부터 받는 데이터
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "user_id는 필수입니다")
    @Size(min = 3, max = 50, message = "user_id는 3자 이상 50자 이하여야 합니다")
    private String userId;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 1, max = 50, message = "이름은 1자 이상 50자 이하여야 합니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String email;

    @Pattern(regexp = "^[0-9-]+$", message = "전화번호는 숫자와 하이픈(-)만 사용할 수 있습니다")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;

    @jakarta.validation.constraints.Min(value = 0, message = "나이는 0 이상이어야 합니다")
    @jakarta.validation.constraints.Max(value = 150, message = "나이는 150 이하여야 합니다")
    private Integer age;

    @Size(max = 200, message = "주소는 200자 이하여야 합니다")
    private String address;

    // id는 제외 (자동 생성되므로)
    // 파일 관련 필드는 제외 (별도 API로 처리)
}
