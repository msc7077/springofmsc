package com.example.springofmsc.domain.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * 
	 */
	private final AgencyNoticeRepository agencyNoticeRepository;

	/**
	 * agency_id로 공지사항 목록 조회
	 * 
	 * [@Transactional(readOnly = true)]
	 * - 읽기 전용 트랜잭션입니다.
	 * - readOnly = true → DataSourceAspect가 자동으로 Slave DB를 사용합니다.
	 * - 읽기 작업이므로 Slave DB로 부하를 분산시킵니다.
	 * 
	 * [왜 readOnly = true인가?]
	 * - 읽기 작업은 데이터를 변경하지 않으므로 읽기 전용으로 설정합니다.
	 * - Master-Slave 구조에서 Slave를 사용합니다.
	 * 
	 * @param agencyId 기관 ID (프론트에서 받는 값)
	 * @return 해당 기관 ID의 공지사항 목록
	 */
	@Transactional(readOnly = true)
	public List<AgencyNotice> findByAgencyId(Integer agencyId) {
		// Repository의 findByAgencyId() 메서드 호출
		// JPA가 자동으로 SELECT * FROM agency_notice WHERE agency_id = ? 쿼리를 실행합니다.
		// Slave DB를 사용합니다 (readOnly = true이므로)
		return agencyNoticeRepository.findByAgencyId(agencyId);
	}
}
