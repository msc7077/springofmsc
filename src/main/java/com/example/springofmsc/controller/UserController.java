package com.example.springofmsc.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springofmsc.dto.UserRequestDTO;
import com.example.springofmsc.dto.UserResponseDTO;
import com.example.springofmsc.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 CRUD API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "전체 사용자 조회", description = "모든 사용자 목록을 조회합니다. 페이징 파라미터를 제공하면 페이징 처리된 결과를 반환합니다.")
    public ResponseEntity<?> getAllUsers(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기 (한 페이지에 표시할 항목 수)", example = "10") @RequestParam(required = false) Integer size,
            @Parameter(description = "정렬 기준 (필드명,asc 또는 필드명,desc)", example = "id,asc") @RequestParam(required = false) String sort) {

        // 페이징 파라미터가 없으면 전체 목록 반환
        if (page == null && size == null && sort == null) {
            List<UserResponseDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }

        // 페이징 파라미터가 있으면 페이징 처리
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Page<UserResponseDTO> userPage = userService.getAllUsers(pageNumber, pageSize, sort);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "사용자 ID로 조회", description = "사용자 ID로 특정 사용자를 조회합니다.")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user-id/{userId}")
    @Operation(summary = "user_id로 사용자 조회", description = "user_id로 특정 사용자를 조회합니다.")
    public ResponseEntity<UserResponseDTO> getUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 사용자 조회", description = "이메일 주소로 사용자를 조회합니다.")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "이름으로 사용자 조회", description = "정확한 이름으로 사용자 목록을 조회합니다.")
    public ResponseEntity<List<UserResponseDTO>> getUsersByName(@PathVariable String name) {
        List<UserResponseDTO> users = userService.getUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/name/contains/{keyword}")
    @Operation(summary = "이름 부분 일치 검색", description = "이름에 특정 키워드가 포함된 사용자 목록을 조회합니다. 페이징 파라미터를 제공하면 페이징 처리된 결과를 반환합니다.")
    public ResponseEntity<?> getUsersByNameContaining(
            @PathVariable String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(required = false) Integer size,
            @Parameter(description = "정렬 기준", example = "name,asc") @RequestParam(required = false) String sort) {

        // 페이징 파라미터가 없으면 전체 목록 반환
        if (page == null && size == null && sort == null) {
            List<UserResponseDTO> users = userService.getUsersByNameContaining(keyword);
            return ResponseEntity.ok(users);
        }

        // 페이징 처리
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Page<UserResponseDTO> userPage = userService.getUsersByNameContaining(keyword, pageNumber, pageSize, sort);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/age/{age}")
    @Operation(summary = "나이로 사용자 조회", description = "나이로 사용자 목록을 조회합니다. 페이징 파라미터를 제공하면 페이징 처리된 결과를 반환합니다.")
    public ResponseEntity<?> getUsersByAge(
            @PathVariable Integer age,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(required = false) Integer size,
            @Parameter(description = "정렬 기준", example = "age,asc") @RequestParam(required = false) String sort) {

        // 페이징 파라미터가 없으면 전체 목록 반환
        if (page == null && size == null && sort == null) {
            List<UserResponseDTO> users = userService.getUsersByAge(age);
            return ResponseEntity.ok(users);
        }

        // 페이징 처리
        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 10 : size;

        Page<UserResponseDTO> userPage = userService.getUsersByAge(age, pageNumber, pageSize, sort);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/search")
    @Operation(summary = "복합 조건 검색", description = "이름, 나이, 휴대번호, 주소를 조건으로 사용하여 사용자를 조회합니다. 모든 조건은 선택사항이며, 조건을 조합하여 검색할 수 있습니다. 페이징 파라미터를 제공하면 페이징 처리된 결과를 반환합니다.")
    public ResponseEntity<?> searchUsers(
            @Parameter(description = "이름 (부분 일치)") @RequestParam(required = false) String name,
            @Parameter(description = "나이 (정확한 일치)") @RequestParam(required = false) Integer age,
            @Parameter(description = "휴대번호 (부분 일치)") @RequestParam(required = false) String phone,
            @Parameter(description = "주소 (부분 일치)") @RequestParam(required = false) String address,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기 (한 페이지에 표시할 항목 수)", example = "10") @RequestParam(required = false) Integer size,
            @Parameter(description = "정렬 기준 (필드명,asc 또는 필드명,desc)", example = "id,asc") @RequestParam(required = false) String sort) {

        // 페이징 파라미터가 없으면 전체 목록 반환
        if (page == null && size == null && sort == null) {
            List<UserResponseDTO> users = userService.searchUsers(name, age, phone, address);
            return ResponseEntity.ok(users);
        }

        // 페이징 처리
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Page<UserResponseDTO> userPage = userService.searchUsers(name, age, phone, address, pageNumber, pageSize, sort);
        return ResponseEntity.ok(userPage);
    }

    @PostMapping
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO savedUser = userService.createUser(requestDTO);
        return ResponseEntity.status(201).body(savedUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "기존 사용자의 정보를 수정합니다.")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, requestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
