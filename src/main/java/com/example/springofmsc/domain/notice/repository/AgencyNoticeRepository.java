package com.example.springofmsc.domain.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springofmsc.domain.notice.dto.AgencyNoticeRequest;
import com.example.springofmsc.domain.notice.entity.AgencyNotice;

/**
 * 기관 공지사항 레포지토리
 */
@Repository
public interface AgencyNoticeRepository extends JpaRepository<AgencyNotice, Integer> {

    @Query(value = """
            SELECT
                n.id AS id,
                CASE
                    WHEN n.is_private = 'N' THEN '📢 전체'
                    WHEN n.is_private = 'S' THEN '💼 직원전용'
                    WHEN n.is_private = 'C' THEN '🌻 반별'
                    WHEN n.is_private = 'M' THEN '🔒 개별'
                END AS division,
                n.subject AS subject,
                (SELECT COUNT(*) FROM agency_notice_files f WHERE f.notice_id = n.id) AS attachCount,
                (SELECT COUNT(*) FROM agency_notice_reads r WHERE r.notice_id = n.id AND r.users_id = :userId) AS readCount
            FROM agency_notice n
            LEFT JOIN agency_notice_allow_classes anc ON n.id = anc.notice_id
            LEFT JOIN agency_class ac ON anc.agency_class_id = ac.id
            LEFT JOIN agency_notice_allow_members anm ON n.id = anm.notice_id
            WHERE n.agency_id = :agencyId
            AND (
                -- 1. [전체]
                n.is_private = 'N'
                OR
                -- 2. [직원] (파라미터로 받은 isStaff 값이 1이면 true)
                (n.is_private = 'S' AND :isStaff = 1)
                OR
                -- 3. [반] (보호자 + 직원 로직 UNION)
                (n.is_private = 'C' AND ac.class_name IN (
                    SELECT p.class_name COLLATE utf8mb4_0900_ai_ci
                    FROM agency_customer_protectors prot
                    JOIN agency_customer_patient_protector map ON prot.id = map.protector_id
                    JOIN agency_customer_patient p ON map.patient_id = p.id
                    WHERE prot.users_id = :userId

                    UNION

                    SELECT p.class_name COLLATE utf8mb4_0900_ai_ci
                    FROM agency_members m
                    JOIN agency_customer_patient p ON m.userid = p.employee_id COLLATE utf8mb4_unicode_ci
                    WHERE m.users_id = :userId
                ))
                OR
                -- 4. [개별]
                (n.is_private = 'M' AND anm.users_id = :userId)
            )
            GROUP BY n.id
            ORDER BY n.id DESC
            """, nativeQuery = true)
    /**
     * 사용자가 조회 가능한 공지사항 목록 조회
     * 
     * @param agencyId 기관 ID
     * @param userId   사용자 ID
     * @param isStaff  직원 여부 (1: 직원, 0: 직원 아님)
     * @return 조회 가능한 공지사항 목록
     */
    List<AgencyNoticeRequest> findAllowedNotices(
            @Param("agencyId") Long agencyId,
            @Param("userId") String userId,
            @Param("isStaff") int isStaff // 1: 직원, 0: 직원아님
    );
}