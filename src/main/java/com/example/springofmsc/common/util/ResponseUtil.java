package com.example.springofmsc.common.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.springofmsc.common.dto.ApiResponse;

/**
 * API 응답 생성 유틸리티
 * ResponseEntity와 ApiResponse를 조합하여 일관된 응답 생성
 */
public class ResponseUtil {

    /**
     * 성공 응답 (200 OK)
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 성공 응답 (200 OK, 커스텀 메시지)
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * 성공 응답 (201 Created)
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "리소스가 성공적으로 생성되었습니다."));
    }

    /**
     * 성공 응답 (201 Created, 커스텀 메시지)
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, message));
    }

    /**
     * 성공 응답 (204 No Content)
     * RESTful 표준에 따라 body 없이 반환
     */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 실패 응답 (400 Bad Request)
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("BAD_REQUEST", message));
    }

    /**
     * 실패 응답 (400 Bad Request, 에러 정보 포함)
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, Map<String, String> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("BAD_REQUEST", message, errors));
    }

    /**
     * 실패 응답 (404 Not Found)
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("NOT_FOUND", message));
    }

    /**
     * 실패 응답 (500 Internal Server Error)
     */
    public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", message));
    }
}
