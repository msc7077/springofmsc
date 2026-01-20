package com.example.springofmsc.domain.file.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.springofmsc.domain.user.entity.User;
import com.example.springofmsc.domain.user.service.UserService;

/**
 * 파일 업로드/다운로드 비즈니스 로직을 처리하는 Service 계층
 */
@Service
@Transactional
public class FileService {

    private final UserService userService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public FileService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 파일 업로드
     */
    public String uploadFile(Long userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 사용자 조회
        User user = userService.getUserEntity(userId);

        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // 파일 저장
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 사용자 정보 업데이트
        user.setFileName(originalFilename);
        user.setFilePath(filePath.toString());
        user.setFileSize(file.getSize());
        user.setFileType(file.getContentType());

        return originalFilename;
    }

    /**
     * 파일 다운로드를 위한 Resource 생성
     */
    @Transactional(readOnly = true)
    public FileDownloadResult downloadFile(Long userId) throws IOException {
        User user = userService.getUserEntity(userId);

        if (user.getFilePath() == null || user.getFileName() == null) {
            throw new IllegalArgumentException("첨부된 파일이 없습니다.");
        }

        Path filePath = Paths.get(user.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }

        // 한글 파일명 인코딩 처리
        String fileName = user.getFileName();
        boolean containsNonAscii = fileName.chars().anyMatch(ch -> ch > 127);

        String contentDisposition;
        if (containsNonAscii) {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            contentDisposition = String.format("attachment; filename*=UTF-8''%s", encodedFileName);
        } else {
            contentDisposition = String.format("attachment; filename=\"%s\"",
                    fileName.replace("\"", "\\\""));
        }

        MediaType mediaType = MediaType.parseMediaType(
                user.getFileType() != null ? user.getFileType() : "application/octet-stream");

        return new FileDownloadResult(resource, mediaType, contentDisposition);
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(Long userId) throws IOException {
        User user = userService.getUserEntity(userId);

        if (user.getFilePath() == null) {
            throw new IllegalArgumentException("첨부된 파일이 없습니다.");
        }

        // 파일 삭제
        Path filePath = Paths.get(user.getFilePath());
        Files.deleteIfExists(filePath);

        // 사용자 정보에서 파일 정보 제거
        user.setFileName(null);
        user.setFilePath(null);
        user.setFileSize(null);
        user.setFileType(null);
    }

    /**
     * 파일 다운로드 결과를 담는 내부 클래스
     */
    public static class FileDownloadResult {
        private final Resource resource;
        private final MediaType mediaType;
        private final String contentDisposition;

        public FileDownloadResult(Resource resource, MediaType mediaType, String contentDisposition) {
            this.resource = resource;
            this.mediaType = mediaType;
            this.contentDisposition = contentDisposition;
        }

        public Resource getResource() {
            return resource;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

        public String getContentDisposition() {
            return contentDisposition;
        }
    }
}
