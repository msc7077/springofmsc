package com.example.springofmsc.domain.notice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springofmsc.domain.notice.dto.AgencyNoticeRequestDTO;
import com.example.springofmsc.domain.notice.dto.AgencyNoticeResponseDTO;
import com.example.springofmsc.domain.notice.dto.PageResponseDTO;
import com.example.springofmsc.domain.notice.entity.AgencyNotice;
import com.example.springofmsc.domain.notice.repository.AgencyNoticeRepository;

import lombok.RequiredArgsConstructor;

/**
 * AgencyNotice Service 클래스
 * 
 * [Service란?]
 * - 비즈니스 로직을 처리하는 계층입니다.
 * - Controller와 Repository 사이에서 중간 역할을 합니다.
 * - 트랜잭션 관리를 여기서 합니다.
 * 
 * [@Service]
 * - 이 클래스가 Service 계층임을 나타냅니다.
 * - Spring이 자동으로 Bean으로 등록합니다.
 */
@RequiredArgsConstructor
@Service
public class AgencyNoticeService {

	/**
	 * Repository 주입
	 */
	private final AgencyNoticeRepository agencyNoticeRepository;

	/**
	 * agency_id로 공지사항 목록 조회 (페이징 처리)
	 * 
	 * [@Transactional(readOnly = true)]
	 * - 읽기 전용 트랜잭션입니다.
	 * - readOnly = true → DataSourceAspect가 자동으로 Slave DB를 사용합니다.
	 * - 읽기 작업이므로 Slave DB로 부하를 분산시킵니다.
	 * 
	 * [페이징 처리]
	 * 1. RequestDTO에서 page, size를 받아서 Pageable 객체 생성
	 * 2. Repository의 페이징 메서드 호출
	 * 3. Page<Entity>를 받아서 PageResponseDTO<ResponseDTO>로 변환
	 * 4. Entity를 ResponseDTO로 변환
	 * 
	 * [PageRequest란?]
	 * - Pageable의 구현체입니다.
	 * - page: 페이지 번호 (0부터 시작)
	 * - size: 페이지 크기
	 * 
	 * [동작 흐름]
	 * 1. RequestDTO에서 agencyId, page, size 받기
	 * 2. PageRequest.of(page, size)로 Pageable 생성
	 * 3. Repository의 findByAgencyId(agencyId, pageable) 호출
	 * 4. Page<AgencyNotice> 받기
	 * 5. Page의 content를 ResponseDTO로 변환
	 * 6. PageResponseDTO로 감싸서 반환
	 * 
	 * @param requestDTO 요청 DTO (agencyId, page, size 포함)
	 * @return 페이징된 공지사항 목록 (PageResponseDTO)
	 */
	@Transactional(readOnly = true)
	public PageResponseDTO<AgencyNoticeResponseDTO> findByAgencyId(AgencyNoticeRequestDTO requestDTO) {
		// 1. 정렬 정보 생성
		// Sort.by(Sort.Direction.DESC, "createdAt"): created_at 기준 내림차순 (최신순)
		// Sort.Direction.DESC: 내림차순 (큰 값부터, 최신순)
		// Sort.Direction.ASC: 오름차순 (작은 값부터, 오래된 순)
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

		// 2. 페이징 정보 생성 (정렬 포함)
		// PageRequest.of(page, size, sort): page는 0부터 시작, 정렬 정보 포함
		Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize(), sort);

		// 3. Repository에서 페이징된 데이터 조회
		// JPA가 자동으로 LIMIT, OFFSET, ORDER BY를 추가한 쿼리 실행
		// 예: SELECT * FROM agency_notice WHERE agency_id = ? ORDER BY created_at DESC
		// LIMIT 10 OFFSET 0
		// Slave DB를 사용합니다 (readOnly = true이므로)
		Page<AgencyNotice> page = agencyNoticeRepository.findByAgencyId(requestDTO.getAgencyId(), pageable);

		// 4. Entity를 ResponseDTO로 변환
		// Page의 content(List<AgencyNotice>)를 List<AgencyNoticeResponseDTO>로 변환
		Page<AgencyNoticeResponseDTO> responsePage = page.map(AgencyNoticeResponseDTO::new);

		// 5. PageResponseDTO로 감싸서 반환
		// 페이징 정보(전체 개수, 전체 페이지 수 등)를 포함하여 반환
		return new PageResponseDTO<>(responsePage);
	}
}
