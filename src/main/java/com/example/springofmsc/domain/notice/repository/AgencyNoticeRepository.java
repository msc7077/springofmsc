package com.example.springofmsc.domain.notice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springofmsc.domain.notice.entity.AgencyNotice;

/**
 * AgencyNotice Repository 인터페이스
 * 
 * [Repository란?]
 * - 데이터베이스에 접근하는 계층입니다.
 * - JPA가 자동으로 구현체를 만들어줍니다.
 * - 우리는 인터페이스만 정의하면 됩니다.
 * 
 * [JpaRepository란?]
 * - Spring Data JPA가 제공하는 인터페이스입니다.
 * - 기본적인 CRUD 메서드들을 자동으로 제공합니다.
 * - <Entity 타입, Primary Key 타입>을 제네릭으로 지정합니다.
 * 
 * [제공되는 기본 메서드]
 * - findAll(): 전체 조회
 * - findById(id): ID로 조회
 * - save(entity): 저장/수정
 * - deleteById(id): 삭제
 */
@Repository
public interface AgencyNoticeRepository extends JpaRepository<AgencyNotice, Integer> {

	/**
	 * agency_id로 공지사항 목록 조회 (페이징 없음)
	 * 
	 * [메서드명 규칙]
	 * - JPA는 메서드명을 분석해서 쿼리를 자동 생성합니다.
	 * - findBy + 필드명(대문자 시작) = SELECT * FROM agency_notice WHERE 필드명 = ?
	 * 
	 * [예시]
	 * - findByAgencyId → WHERE agency_id = ?
	 * 
	 * [반환 타입]
	 * - List<AgencyNotice>: 여러 개 조회 (같은 agency_id가 여러 개일 수 있으므로)
	 * 
	 * @param agencyId 기관 ID (프론트에서 받는 값)
	 * @return 해당 기관 ID의 공지사항 목록
	 */
	List<AgencyNotice> findByAgencyId(Integer agencyId);

	/**
	 * agency_id로 공지사항 목록 조회 (페이징 처리)
	 * 
	 * [Pageable이란?]
	 * - Spring Data JPA가 제공하는 페이징 인터페이스입니다.
	 * - page, size, sort 등의 정보를 담고 있습니다.
	 * - Controller에서 받은 page, size를 Pageable로 변환하여 전달합니다.
	 * 
	 * [Page란?]
	 * - 페이징된 결과를 담는 객체입니다.
	 * - content: 실제 데이터 목록
	 * - totalElements: 전체 데이터 개수
	 * - totalPages: 전체 페이지 개수
	 * - 등등 페이징에 필요한 모든 정보를 포함합니다.
	 * 
	 * [메서드명 규칙]
	 * - findByAgencyId + Pageable 파라미터 = 페이징 쿼리 자동 생성
	 * - JPA가 자동으로 LIMIT, OFFSET을 추가합니다.
	 * 
	 * [예시]
	 * - page = 0, size = 10 → LIMIT 10 OFFSET 0
	 * - page = 1, size = 10 → LIMIT 10 OFFSET 10
	 * 
	 * @param agencyId 기관 ID
	 * @param pageable 페이징 정보 (page, size)
	 * @return 페이징된 공지사항 목록
	 */
	Page<AgencyNotice> findByAgencyId(Integer agencyId, Pageable pageable);
}
