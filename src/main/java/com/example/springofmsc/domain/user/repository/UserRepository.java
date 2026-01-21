package com.example.springofmsc.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springofmsc.domain.user.entity.User;

/**
 * 사용자 레포지토리
 * 실제 DB 스키마에 맞춘 구조
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        // userid로 사용자 조회
        Optional<User> findByUserid(String userid);

        // 이름으로 사용자 목록 조회 (정확한 일치)
        List<User> findByName(String name);

        // 이름에 포함된 문자열로 사용자 목록 조회 (부분 일치)
        List<User> findByNameContaining(String name);

        // 이름으로 시작하는 사용자 목록 조회
        List<User> findByNameStartingWith(String name);

        // 이름으로 끝나는 사용자 목록 조회
        List<User> findByNameEndingWith(String name);

        // 계정 상태로 사용자 조회
        List<User> findByStatus(String status);

        // 계정 유형으로 사용자 조회
        List<User> findByAccountType(User.AccountType accountType);

        // 복합 조건 검색 (이름, 계정 상태, 계정 유형) - 페이징 지원
        @Query(value = "SELECT * FROM users WHERE " +
                        "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
                        "(:status IS NULL OR status = :status) AND " +
                        "(:accountType IS NULL OR account_type = :accountType)", countQuery = "SELECT COUNT(*) FROM users WHERE "
                                        +
                                        "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
                                        "(:status IS NULL OR status = :status) AND " +
                                        "(:accountType IS NULL OR account_type = :accountType)", nativeQuery = true)
        Page<User> searchUsers(
                        @Param("name") String name,
                        @Param("status") String status,
                        @Param("accountType") String accountType,
                        Pageable pageable);

        // 페이징 처리를 위한 전체 사용자 조회
        Page<User> findAll(Pageable pageable);

        // 이름으로 페이징 조회
        Page<User> findByNameContaining(String name, Pageable pageable);

        // 계정 상태로 페이징 조회
        Page<User> findByStatus(String status, Pageable pageable);
}
