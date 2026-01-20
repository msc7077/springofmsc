package com.example.springofmsc.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 조회 응답 DTO
 * 클라이언트에게 반환하는 데이터
 * 불필요한 정보(filePath 등)는 제외
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String userId;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private String address;
    private String fileName;      // 원본 파일명만 제공
    private Long fileSize;        // 파일 크기
    private String fileType;       // 파일 타입
    // filePath는 제외 (서버 내부 경로이므로 클라이언트에게 불필요)
}
