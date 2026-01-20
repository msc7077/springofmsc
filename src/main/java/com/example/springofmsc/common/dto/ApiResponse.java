package com.example.springofmsc.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 통일된 API 응답 형식
 * 모든 API 응답을 일관된 형식으로 래핑
 * 
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;              // 성공 여부
    private String code;                  // 응답 코드 (예: "SUCCESS", "NOT_FOUND", "VALIDATION_ERROR")
    private String message;               // 응답 메시지
    private T data;                       // 실제 데이터 (성공 시)
    private Map<String, String> errors;   // 에러 정보 (실패 시, 필드별 에러 등)
    private LocalDateTime timestamp;      // 응답 시간

    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                "요청이 성공적으로 처리되었습니다.",
                data,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                message,
                data,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                message,
                null,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(
                false,
                code,
                message,
                null,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * 실패 응답 생성 (에러 정보 포함)
     */
    public static <T> ApiResponse<T> error(String code, String message, Map<String, String> errors) {
        return new ApiResponse<>(
                false,
                code,
                message,
                null,
                errors,
                LocalDateTime.now()
        );
    }
}
