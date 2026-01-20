package com.example.springofmsc.dto;

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
    private String userId;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private String address;
    // id는 제외 (자동 생성되므로)
    // 파일 관련 필드는 제외 (별도 API로 처리)
}
