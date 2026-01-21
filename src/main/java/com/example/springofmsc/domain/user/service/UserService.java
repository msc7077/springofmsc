package com.example.springofmsc.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springofmsc.domain.user.dto.UserRequestDTO;
import com.example.springofmsc.domain.user.dto.UserResponseDTO;
import com.example.springofmsc.domain.user.entity.User;
import com.example.springofmsc.domain.user.repository.UserRepository;

/**
 * 사용자 비즈니스 로직을 처리하는 Service 계층
 * 실제 DB 스키마에 맞춘 구조
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 전체 사용자 조회
     */
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * 전체 사용자 조회 (페이징)
     */
    public Page<UserResponseDTO> getAllUsers(int page, int size, String sort) {
        Sort sortObj = createSort(sort, "id");
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(this::toResponseDTO);
    }

    /**
     * ID로 사용자 조회
     */
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponseDTO);
    }

    /**
     * userid로 사용자 조회
     */
    public Optional<UserResponseDTO> getUserByUserid(String userid) {
        return userRepository.findByUserid(userid)
                .map(this::toResponseDTO);
    }

    /**
     * 이름으로 사용자 조회
     */
    public List<UserResponseDTO> getUsersByName(String name) {
        List<User> users = userRepository.findByName(name);
        return users.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * 이름 부분 일치 검색
     */
    public List<UserResponseDTO> getUsersByNameContaining(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        return users.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * 이름 부분 일치 검색 (페이징)
     */
    public Page<UserResponseDTO> getUsersByNameContaining(String keyword, int page, int size, String sort) {
        Sort sortObj = createSort(sort, "name");
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<User> userPage = userRepository.findByNameContaining(keyword, pageable);
        return userPage.map(this::toResponseDTO);
    }

    /**
     * 계정 상태로 사용자 조회
     */
    public List<UserResponseDTO> getUsersByStatus(String status) {
        List<User> users = userRepository.findByStatus(status);
        return users.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * 계정 상태로 사용자 조회 (페이징)
     */
    public Page<UserResponseDTO> getUsersByStatus(String status, int page, int size, String sort) {
        Sort sortObj = createSort(sort, "id");
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<User> userPage = userRepository.findByStatus(status, pageable);
        return userPage.map(this::toResponseDTO);
    }

    /**
     * 복합 조건 검색
     */
    public List<UserResponseDTO> searchUsers(String name, String status, String accountType) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<User> userPage = userRepository.searchUsers(name, status, accountType, pageable);
        return userPage.getContent().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * 복합 조건 검색 (페이징)
     */
    public Page<UserResponseDTO> searchUsers(String name, String status, String accountType,
            int page, int size, String sort) {
        Sort sortObj = createSort(sort, "id");
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<User> userPage = userRepository.searchUsers(name, status, accountType, pageable);
        return userPage.map(this::toResponseDTO);
    }

    /**
     * 사용자 생성
     */
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        // userid 중복 체크
        if (userRepository.findByUserid(requestDTO.getUserid()).isPresent()) {
            throw new IllegalArgumentException("User ID already exists: " + requestDTO.getUserid());
        }

        User user = toEntity(requestDTO);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return toResponseDTO(savedUser);
    }

    /**
     * 사용자 수정
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // userid 변경 시 중복 체크
        if (requestDTO.getUserid() != null && !user.getUserid().equals(requestDTO.getUserid())) {
            if (userRepository.findByUserid(requestDTO.getUserid()).isPresent()) {
                throw new IllegalArgumentException("User ID already exists: " + requestDTO.getUserid());
            }
            user.setUserid(requestDTO.getUserid());
        }

        // 정보 업데이트
        if (requestDTO.getName() != null) {
            user.setName(requestDTO.getName());
        }
        if (requestDTO.getUserType() != null) {
            user.setUserType(requestDTO.getUserType());
        }
        if (requestDTO.getAccountType() != null) {
            user.setAccountType(requestDTO.getAccountType());
        }
        if (requestDTO.getUserci() != null) {
            user.setUserci(requestDTO.getUserci());
        }
        if (requestDTO.getUserdi() != null) {
            user.setUserdi(requestDTO.getUserdi());
        }
        if (requestDTO.getBusinessNumber() != null) {
            user.setBusinessNumber(requestDTO.getBusinessNumber());
        }
        if (requestDTO.getIsAdmin() != null) {
            user.setIsAdmin(requestDTO.getIsAdmin());
        }
        if (requestDTO.getStatus() != null) {
            user.setStatus(requestDTO.getStatus());
            user.setStatusAt(LocalDateTime.now());
        }
        if (requestDTO.getExpired() != null) {
            user.setExpired(requestDTO.getExpired());
            if (requestDTO.getExpired() == User.YesNo.Y) {
                user.setExpiredAt(LocalDateTime.now());
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return toResponseDTO(updatedUser);
    }

    /**
     * 사용자 삭제
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Entity를 조회하기 위한 내부 메서드 (FileService 등에서 사용)
     */
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    /**
     * Entity를 조회하기 위한 내부 메서드 (Optional 반환)
     */
    public Optional<User> getUserEntityOptional(Long id) {
        return userRepository.findById(id);
    }

    // ========== 변환 메서드 ==========

    /**
     * Entity → ResponseDTO 변환
     */
    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUserid(),
                user.getName(),
                user.getUserType(),
                user.getAccountType(),
                user.getUserci(),
                user.getUserdi(),
                user.getBusinessNumber(),
                user.getIsAdmin(),
                user.getStatus(),
                user.getStatusAt(),
                user.getExpired(),
                user.getExpiredAt(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    /**
     * RequestDTO → Entity 변환
     */
    private User toEntity(UserRequestDTO requestDTO) {
        User user = new User();
        user.setUserid(requestDTO.getUserid());
        user.setName(requestDTO.getName());
        user.setUserType(requestDTO.getUserType());
        user.setAccountType(requestDTO.getAccountType());
        user.setUserci(requestDTO.getUserci());
        user.setUserdi(requestDTO.getUserdi());
        user.setBusinessNumber(requestDTO.getBusinessNumber());
        user.setIsAdmin(requestDTO.getIsAdmin() != null ? requestDTO.getIsAdmin() : User.YesNo.N);
        user.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : "A");
        user.setExpired(requestDTO.getExpired() != null ? requestDTO.getExpired() : User.YesNo.N);
        return user;
    }

    /**
     * 정렬 객체 생성
     */
    private Sort createSort(String sort, String defaultField) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, defaultField);
        }

        String[] sortParams = sort.split(",");
        if (sortParams.length == 2) {
            String field = sortParams[0].trim();
            String direction = sortParams[1].trim().toLowerCase();
            return Sort.by(
                    "desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                    field);
        }

        return Sort.by(Sort.Direction.ASC, defaultField);
    }
}
