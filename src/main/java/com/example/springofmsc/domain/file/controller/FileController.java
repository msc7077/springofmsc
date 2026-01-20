package com.example.springofmsc.domain.file.controller;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.springofmsc.domain.file.service.FileService;
import com.example.springofmsc.domain.file.service.FileService.FileDownloadResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "File", description = "파일 업로드/다운로드 API")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드", description = "사용자에게 파일을 첨부합니다. 'Try it out' 버튼을 클릭한 후 'file' 파라미터에서 파일을 선택하세요.")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "사용자 ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "업로드할 파일", required = true) @RequestParam("file") MultipartFile file) {

        try {
            String fileName = fileService.uploadFile(id, file);
            return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다: " + fileName);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 업로드 실패: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/file")
    @Operation(summary = "파일 다운로드", description = "사용자에게 첨부된 파일을 다운로드합니다.")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            FileDownloadResult result = fileService.downloadFile(id);
            return ResponseEntity.ok()
                    .contentType(result.getMediaType())
                    .header(HttpHeaders.CONTENT_DISPOSITION, result.getContentDisposition())
                    .body(result.getResource());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/file/delete")
    @Operation(summary = "파일 삭제", description = "사용자에게 첨부된 파일을 삭제합니다.")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 삭제 실패: " + e.getMessage());
        }
    }
}
