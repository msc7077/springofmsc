package com.example.springofmsc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springofmsc.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // user_id로 사용자 조회
    Optional<User> findByUserId(String userId);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 이름으로 사용자 목록 조회 (정확한 일치)
    List<User> findByName(String name);

    // 이름에 포함된 문자열로 사용자 목록 조회 (부분 일치)
    List<User> findByNameContaining(String name);

    // 이름으로 시작하는 사용자 목록 조회
    List<User> findByNameStartingWith(String name);

    // 이름으로 끝나는 사용자 목록 조회
    List<User> findByNameEndingWith(String name);

    // 나이로 사용자 목록 조회
    List<User> findByAge(Integer age);

    // 이름과 나이로 사용자 조회 (복합 조건)
    List<User> findByNameAndAge(String name, Integer age);

    // 복합 조건 검색 (이름, 나이, 휴대번호, 주소) - 페이징 지원
    @Query(value = "SELECT * FROM users WHERE " +
            "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
            "(:age IS NULL OR age = :age) AND " +
            "(:phone IS NULL OR phone LIKE CONCAT('%', :phone, '%')) AND " +
            "(:address IS NULL OR address LIKE CONCAT('%', :address, '%'))", countQuery = "SELECT COUNT(*) FROM users WHERE "
                    +
                    "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
                    "(:age IS NULL OR age = :age) AND " +
                    "(:phone IS NULL OR phone LIKE CONCAT('%', :phone, '%')) AND " +
                    "(:address IS NULL OR address LIKE CONCAT('%', :address, '%'))", nativeQuery = true)
    Page<User> searchUsers(
            @Param("name") String name,
            @Param("age") Integer age,
            @Param("phone") String phone,
            @Param("address") String address,
            Pageable pageable);

    // 페이징 처리를 위한 전체 사용자 조회
    Page<User> findAll(Pageable pageable);

    // 이름으로 페이징 조회
    Page<User> findByNameContaining(String name, Pageable pageable);

    // 나이로 페이징 조회
    Page<User> findByAge(Integer age, Pageable pageable);
}
