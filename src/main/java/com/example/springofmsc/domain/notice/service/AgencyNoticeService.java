package com.example.springofmsc.domain.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.springofmsc.domain.notice.dto.AgencyNoticeRequest;
import com.example.springofmsc.domain.notice.dto.AgencyNoticeResponseDTO;
import com.example.springofmsc.domain.notice.repository.AgencyMembersRepository;
import com.example.springofmsc.domain.notice.repository.AgencyNoticeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 기관 공지사항 서비스
 */
@Service
@RequiredArgsConstructor
public class AgencyNoticeService {

    private final AgencyNoticeRepository agencyNoticeRepository;
    private final AgencyMembersRepository agencyMembersRepository;

    /**
     * 사용자가 조회 가능한 공지사항 목록 조회
     * 
     * @param agencyId 기관 ID
     * @param userId   사용자 ID
     * @return 조회 가능한 공지사항 목록
     */
    public List<AgencyNoticeResponseDTO> getNoticeList(Long agencyId, String userId) {
        // 1. 이 사람이 직원인지 체크 (1 or 0)
        // userId는 사용자 로그인 ID (예: tester12)
        boolean isStaffBool = agencyMembersRepository.existsByAgencyIdAndUserid(agencyId, userId);
        int isStaff = isStaffBool ? 1 : 0;

        // 2. 쿼리 실행
        List<AgencyNoticeRequest> notices = agencyNoticeRepository.findAllowedNotices(agencyId, userId, isStaff);

        // 3. AgencyNoticeRequest를 AgencyNoticeResponseDTO로 변환
        return notices.stream()
                .map(notice -> new AgencyNoticeResponseDTO(
                        notice.getId(),
                        notice.getDivision(),
                        notice.getSubject(),
                        notice.getAttachCount(),
                        notice.getReadCount()))
                .collect(Collectors.toList());
    }
}