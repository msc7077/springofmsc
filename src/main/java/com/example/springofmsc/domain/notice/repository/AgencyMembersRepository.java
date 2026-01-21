package com.example.springofmsc.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springofmsc.domain.notice.entity.AgencyMember;

/**
 * 기관 직원 레포지토리
 */
@Repository
public interface AgencyMembersRepository extends JpaRepository<AgencyMember, Long> {

    /**
     * 특정 기관의 특정 사용자가 직원인지 확인
     * 
     * @param agencyId 기관 ID
     * @param userid   사용자 로그인 ID (예: tester12)
     * @return 직원 여부
     */
    boolean existsByAgencyIdAndUserid(Long agencyId, String userid);
}
