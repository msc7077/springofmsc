package com.example.springofmsc.domain.user.controller;

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

import com.example.springofmsc.common.dto.ApiResponse;
import com.example.springofmsc.common.util.ResponseUtil;
import com.example.springofmsc.domain.user.dto.UserRequestDTO;
import com.example.springofmsc.domain.user.dto.UserResponseDTO;
import com.example.springofmsc.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 사용자 컨트롤러
 * 실제 DB 스키마에 맞춘 구조
 */
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
            return ResponseUtil.ok(users, "사용자 목록 조회 성공");
        }

        // 페이징 파라미터가 있으면 페이징 처리
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Page<UserResponseDTO> userPage = userService.getAllUsers(pageNumber, pageSize, sort);
        return ResponseUtil.ok(userPage, "사용자 목록 조회 성공 (페이징)");
    }

    @GetMapping("/{id}")
    @Operation(summary = "사용자 ID로 조회", description = "사용자 ID로 특정 사용자를 조회합니다.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseUtil.ok(user, "사용자 조회 성공"))
                .orElse(ResponseUtil.notFound("사용자를 찾을 수 없습니다."));
    }

    @GetMapping("/userid/{userid}")
    @Operation(summary = "userid로 사용자 조회", description = "userid로 특정 사용자를 조회합니다.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUserid(@PathVariable String userid) {
        return userService.getUserByUserid(userid)
                .map(user -> ResponseUtil.ok(user, "사용자 조회 성공"))
                .orElse(ResponseUtil.notFound("사용자를 찾을 수 없습니다."));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "이름으로 사용자 조회", description = "정확한 이름으로 사용자 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsersByName(@PathVariable String name) {
        List<UserResponseDTO> users = userService.getUsersByName(name);
        return ResponseUtil.ok(users, "사용자 목록 조회 성공");
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
            return ResponseUtil.ok(users, "사용자 검색 성공");
        }

        // 페이징 처리
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Page<UserResponseDTO> userPage = userService.getUsersByNameContaining(keyword, pageNumber, pageSize, sort);
        return ResponseUtil.ok(userPage, "사용자 검색 성공 (페이징)");
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "계정 상태로 사용자 조회", description = "계정 상태로 사용자 목록을 조회합니다. 페이징 파라미터를 제공하면 페이징 처리된 결과를 반환합니다.")
    public ResponseEntity<?> getUsersByStatus(
            @PathVariable String status,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(required = false) Integer size,
            @Parameter(description = "정렬 기준", example = "id,asc") @RequestParam(required = false) String sort) {

        // 페이징 파라미터가 없으면 전체 목록 반환
        if (page == null && size == null && sort == null) {
            List<UserResponseDTO> users = userService.getUsersByStatus(status);
            return ResponseUtil.ok(users, "사용자 조회 성공");
        }

        // 페이징 처리
        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 10 : size;

        Page<UserResponseDTO> userPage = userService.getUsersByStatus(status, pageNumber, pageSize, sort);
        return ResponseUtil.ok(userPage, "사용자 조회 성공 (페이징)");
    }

    @GetMapping("/search")
    @Operation(summary = "복합 조건 검색", description = "이름, 계정 상태, 계정 유형을 조건으로 사용하여 사용자를 조회합니다. 모든 조건은 선택사항이며, 조건을 조합하여 검색할 수 있습니다. 페이징 파라미터를 제공하면 페이징 처리된 결과를 반환합니다.")
    public ResponseEntity<?> searchUsers(
            @Parameter(description = "이름 (부분 일치)") @RequestParam(required = false) String name,
            @Parameter(description = "계정 상태 (A:활성화, D:탈퇴, R:휴면, H:보류)") @RequestParam(required = false) String status,
            @Parameter(description = "계정 유형 (AGENCY, GENERATED, PERSONAL, PERSONAL_AGENCY_MEMBER, PERSONAL_GUARDIAN)") @RequestParam(required = false) String accountType,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기 (한 페이지에 표시할 항목 수)", example = "10") @RequestParam(required = false) Integer size,
            @Parameter(description = "정렬 기준 (필드명,asc 또는 필드명,desc)", example = "id,asc") @RequestParam(required = false) String sort) {

        // 페이징 파라미터가 없으면 전체 목록 반환
        if (page == null && size == null && sort == null) {
            List<UserResponseDTO> users = userService.searchUsers(name, status, accountType);
            return ResponseUtil.ok(users, "사용자 검색 성공");
        }

        // 페이징 처리
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Page<UserResponseDTO> userPage = userService.searchUsers(name, status, accountType, pageNumber, pageSize, sort);
        return ResponseUtil.ok(userPage, "사용자 검색 성공 (페이징)");
    }

    @PostMapping
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO savedUser = userService.createUser(requestDTO);
        return ResponseUtil.created(savedUser, "사용자가 성공적으로 생성되었습니다.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "기존 사용자의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, requestDTO);
        return ResponseUtil.ok(updatedUser, "사용자 정보가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseUtil.noContent();
    }
}
