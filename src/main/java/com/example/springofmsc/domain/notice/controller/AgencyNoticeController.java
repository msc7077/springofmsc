package com.example.springofmsc.domain.notice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springofmsc.common.dto.ApiResponse;
import com.example.springofmsc.common.util.ResponseUtil;
import com.example.springofmsc.domain.notice.dto.AgencyNoticeResponseDTO;
import com.example.springofmsc.domain.notice.service.AgencyNoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 기관 공지사항 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
@Tag(name = "AgencyNotice", description = "기관 공지사항 API")
public class AgencyNoticeController {

    private final AgencyNoticeService agencyNoticeService;

    @GetMapping
    @Operation(summary = "공지사항 목록 조회", description = "특정 기관의 특정 사용자가 조회 가능한 공지사항 목록을 조회합니다. 권한에 따라 전체, 직원전용, 반별, 개별 공지사항이 필터링되어 반환됩니다.")
    public ResponseEntity<ApiResponse<List<AgencyNoticeResponseDTO>>> getNoticeList(
            @Parameter(description = "기관 ID", required = true, example = "1") @RequestParam Long agencyId,
            @Parameter(description = "사용자 로그인 ID (예: tester12)", required = true, example = "tester12") @RequestParam String userId) {

        List<AgencyNoticeResponseDTO> notices = agencyNoticeService.getNoticeList(agencyId, userId);
        return ResponseUtil.ok(notices, "공지사항 목록 조회 성공");
    }
}
