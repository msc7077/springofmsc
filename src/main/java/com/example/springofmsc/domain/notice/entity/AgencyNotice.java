package com.example.springofmsc.domain.notice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 기관 공지사항 엔티티
 */
@Entity
@Table(name = "agency_notice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgencyNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // DB 테이블이 INT 타입이므로 Integer 사용

    @Column(name = "agency_id")
    private Integer agencyId; // DB 테이블이 INT 타입이므로 Integer 사용

    @Column(name = "is_private", columnDefinition = "CHAR(1)")
    private String isPrivate; // N: 전체, S: 직원전용, C: 반별, M: 개별

    @Column(name = "subject")
    private String subject;
}
