package com.example.springofmsc.domain.notice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springofmsc.domain.notice.entity.AgencyNotice;
import com.example.springofmsc.domain.notice.service.AgencyNoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * AgencyNotice Controller 클래스
 * 
 * [Controller란?]
 * - HTTP 요청을 받아서 처리하는 계층입니다.
 * - 클라이언트(프론트엔드)와 서버를 연결하는 역할입니다.
 * - REST API 엔드포인트를 정의합니다.
 * 
 * [@RestController]
 * - @Controller + @ResponseBody의 조합입니다.
 * - 메서드의 반환값을 자동으로 JSON으로 변환합니다.
 * - REST API를 만들 때 사용합니다.
 * 
 * [@RequestMapping]
 * - 클래스 레벨에서 공통 URL 경로를 지정합니다.
 * - 예: @RequestMapping("/api/notices") → 모든 메서드의 URL이 /api/notices로 시작
 * 
 * [@Tag]
 * - Swagger UI에서 이 컨트롤러를 그룹으로 묶어서 표시합니다.
 * - name: 그룹 이름
 * - description: 그룹 설명
 */
@Tag(name = "공지사항 API", description = "기관 공지사항 조회 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notices")
public class AgencyNoticeController {

	/**
	 * Service 주입
	 */
	private final AgencyNoticeService agencyNoticeService;

	/**
	 * agency_id로 공지사항 목록 조회
	 * 
	 * [@GetMapping]
	 * - GET 요청을 처리합니다.
	 * - URL: GET /api/notices?agencyId=123
	 * 
	 * [@RequestParam]
	 * - 쿼리 파라미터를 받습니다.
	 * - 예: GET /api/notices?agencyId=123 → agencyId = 123
	 * 
	 * [required = true]
	 * - 파라미터가 필수입니다.
	 * - 없으면 400 Bad Request 에러가 발생합니다.
	 * 
	 * [동작 흐름]
	 * 1. 프론트에서 GET /api/notices?agencyId=123 요청
	 * 2. 이 메서드가 실행됨
	 * 3. Service의 findByAgencyId() 호출
	 * 4. Repository가 DB에서 조회 (Slave DB 사용)
	 * 5. 결과를 JSON으로 변환하여 반환
	 * 
	 * [참고: 나중에 할 작업]
	 * - 현재는 로그인한 사용자 정보(id: 24, userid: tester)를 하드코딩
	 * - 나중에 token에서 사용자 정보를 추출할 예정
	 * - token에서 추출한 사용자 정보로 권한 체크 등을 할 수 있음
	 * 
	 * [@Operation]
	 * - Swagger UI에 표시될 API 설명을 작성합니다.
	 * - summary: 간단한 설명
	 * - description: 자세한 설명
	 * 
	 * [@Parameter]
	 * - Swagger UI에 표시될 파라미터 설명을 작성합니다.
	 * - name: 파라미터 이름
	 * - description: 파라미터 설명
	 * - required: 필수 여부
	 * 
	 * @param agencyId 기관 ID (프론트에서 받는 값, 필수)
	 * @return 해당 기관 ID의 공지사항 목록 (200 OK)
	 */
	@Operation(summary = "기관 공지사항 조회", description = "agency_id로 해당 기관의 공지사항 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<List<AgencyNotice>> getNoticesByAgencyId(
			@Parameter(description = "기관 ID", required = true, example = "123") @RequestParam(required = true) Integer agencyId) {

		// [참고] 나중에 token에서 사용자 정보 추출
		// 현재는 하드코딩: id = 24, userid = "tester"
		// Long userId = 24L;
		// String userid = "tester";
		// token에서 추출한 사용자 정보로 권한 체크 등을 할 수 있음

		// Service를 통해 agency_id로 공지사항 목록 조회
		// readOnly = true이므로 Slave DB를 사용합니다
		List<AgencyNotice> notices = agencyNoticeService.findByAgencyId(agencyId);

		// 200 OK 상태 코드와 함께 결과 반환
		// List<AgencyNotice>는 자동으로 JSON 배열로 변환됩니다
		return ResponseEntity.ok(notices);
	}
}
