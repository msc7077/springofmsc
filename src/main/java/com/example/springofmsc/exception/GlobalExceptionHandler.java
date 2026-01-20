package com.example.springofmsc.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.springofmsc.common.dto.ApiResponse;
import com.example.springofmsc.common.util.ResponseUtil;

/**
 * 전역 예외 처리기
 * 모든 Controller에서 발생하는 예외를 일관되게 처리
 */
@ControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Validation 오류 처리
         * 
         * @Valid 어노테이션이 적용된 파라미터의 검증 실패 시 발생
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException e) {

                // 각 필드별 오류 메시지 수집
                Map<String, String> fieldErrors = new HashMap<>();
                for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
                        fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
                }

                return ResponseUtil.badRequest(
                                "요청 데이터가 유효성 검증을 통과하지 못했습니다.",
                                fieldErrors);
        }

        /**
         * JSON 파싱 오류 처리
         * 잘못된 JSON 형식이 전달되었을 때 발생
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException e) {

                String message = "요청 본문의 JSON 형식이 올바르지 않습니다. " +
                                "다음 사항을 확인해주세요:\n" +
                                "1. JSON 형식이 올바른지 확인 (쉼표, 따옴표 등)\n" +
                                "2. 마지막에 불필요한 쉼표가 없는지 확인\n" +
                                "3. 빈 값이 없는지 확인";

                return ResponseUtil.badRequest(message);
        }

        /**
         * IllegalArgumentException 처리
         * 비즈니스 로직에서 발생하는 예외
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
                        IllegalArgumentException e) {

                // "not found" 메시지가 포함된 경우 404 반환
                if (e.getMessage() != null && e.getMessage().contains("not found")) {
                        return ResponseUtil.notFound(e.getMessage());
                }

                return ResponseUtil.badRequest(e.getMessage());
        }

        /**
         * IOException 처리
         * 파일 입출력 작업에서 발생하는 예외
         */
        @ExceptionHandler(IOException.class)
        public ResponseEntity<ApiResponse<Object>> handleIOException(IOException e) {
                return ResponseUtil.internalServerError("파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
}
