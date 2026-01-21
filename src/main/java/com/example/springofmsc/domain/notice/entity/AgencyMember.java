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
 * 기관 직원 엔티티
 */
@Entity
@Table(name = "agency_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgencyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agency_id")
    private Long agencyId;

    @Column(name = "users_id")
    private Long usersId;

    @Column(name = "userid")
    private String userid;
}
