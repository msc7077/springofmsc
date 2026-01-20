package com.example.springofmsc.domain.user.service;

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
     * user_id로 사용자 조회
     */
    public Optional<UserResponseDTO> getUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .map(this::toResponseDTO);
    }

    /**
     * 이메일로 사용자 조회
     */
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
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
     * 나이로 사용자 조회
     */
    public List<UserResponseDTO> getUsersByAge(Integer age) {
        List<User> users = userRepository.findByAge(age);
        return users.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * 나이로 사용자 조회 (페이징)
     */
    public Page<UserResponseDTO> getUsersByAge(Integer age, int page, int size, String sort) {
        Sort sortObj = createSort(sort, "id");
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<User> userPage = userRepository.findByAge(age, pageable);
        return userPage.map(this::toResponseDTO);
    }

    /**
     * 복합 조건 검색
     */
    public List<UserResponseDTO> searchUsers(String name, Integer age, String phone, String address) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<User> userPage = userRepository.searchUsers(name, age, phone, address, pageable);
        return userPage.getContent().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * 복합 조건 검색 (페이징)
     */
    public Page<UserResponseDTO> searchUsers(String name, Integer age, String phone, String address,
            int page, int size, String sort) {
        Sort sortObj = createSort(sort, "id");
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<User> userPage = userRepository.searchUsers(name, age, phone, address, pageable);
        return userPage.map(this::toResponseDTO);
    }

    /**
     * 사용자 생성
     */
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        // user_id 중복 체크
        if (userRepository.findByUserId(requestDTO.getUserId()).isPresent()) {
            throw new IllegalArgumentException("User ID already exists: " + requestDTO.getUserId());
        }

        // email 중복 체크
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + requestDTO.getEmail());
        }

        User user = toEntity(requestDTO);
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

        // user_id 변경 시 중복 체크
        if (!user.getUserId().equals(requestDTO.getUserId())) {
            if (userRepository.findByUserId(requestDTO.getUserId()).isPresent()) {
                throw new IllegalArgumentException("User ID already exists: " + requestDTO.getUserId());
            }
        }

        // email 변경 시 중복 체크
        if (!user.getEmail().equals(requestDTO.getEmail())) {
            if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists: " + requestDTO.getEmail());
            }
        }

        // 정보 업데이트
        user.setUserId(requestDTO.getUserId());
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());
        user.setAge(requestDTO.getAge());
        user.setAddress(requestDTO.getAddress());

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
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAge(),
                user.getAddress(),
                user.getFileName(),
                user.getFileSize(),
                user.getFileType()
        );
    }

    /**
     * RequestDTO → Entity 변환
     */
    private User toEntity(UserRequestDTO requestDTO) {
        User user = new User();
        user.setUserId(requestDTO.getUserId());
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());
        user.setAge(requestDTO.getAge());
        user.setAddress(requestDTO.getAddress());
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
                    field
            );
        }

        return Sort.by(Sort.Direction.ASC, defaultField);
    }
}
