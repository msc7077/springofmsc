package com.example.springofmsc.domain.notice.repository;

import com.example.springofmsc.domain.notice.entity.AgencyNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
	 * agency_id로 공지사항 목록 조회
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
}
