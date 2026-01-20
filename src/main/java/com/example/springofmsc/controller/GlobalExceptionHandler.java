package com.example.springofmsc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리기
 * 모든 Controller에서 발생하는 예외를 일관되게 처리
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * JSON 파싱 오류 처리
     * 잘못된 JSON 형식이 전달되었을 때 발생
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "JSON 파싱 오류");
        errorResponse.put("message", "요청 본문의 JSON 형식이 올바르지 않습니다. " + 
                "다음 사항을 확인해주세요:\n" +
                "1. JSON 형식이 올바른지 확인 (쉼표, 따옴표 등)\n" +
                "2. 마지막에 불필요한 쉼표가 없는지 확인\n" +
                "3. 빈 값이 없는지 확인");
        errorResponse.put("detail", e.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * IllegalArgumentException 처리
     * 비즈니스 로직에서 발생하는 예외
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "잘못된 요청");
        errorResponse.put("message", e.getMessage());
        
        // "not found" 메시지가 포함된 경우 404 반환
        if (e.getMessage() != null && e.getMessage().contains("not found")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
