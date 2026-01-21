package com.example.springofmsc.domain.file.controller;

// @RestController
// @RequestMapping("/api/users")
// @Tag(name = "File", description = "파일 업로드/다운로드 API")
// public class FileController {

// private final FileService fileService;

// public FileController(FileService fileService) {
// this.fileService = fileService;
// }

// @PostMapping(value = "/{id}/file", consumes =
// MediaType.MULTIPART_FORM_DATA_VALUE)
// @Operation(summary = "파일 업로드", description = "사용자에게 파일을 첨부합니다. 'Try it out'
// 버튼을 클릭한 후 'file' 파라미터에서 파일을 선택하세요.")
// public ResponseEntity<ApiResponse<String>> uploadFile(
// @Parameter(description = "사용자 ID", required = true, example = "1")
// @PathVariable Long id,
// @Parameter(description = "업로드할 파일", required = true) @RequestParam("file")
// MultipartFile file)
// throws IOException {

// String fileName = fileService.uploadFile(id, file);
// return ResponseUtil.ok("파일이 성공적으로 업로드되었습니다: " + fileName, "파일 업로드 성공");
// }

// @GetMapping("/{id}/file")
// @Operation(summary = "파일 다운로드", description = "사용자에게 첨부된 파일을 다운로드합니다.")
// public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws
// IOException {
// FileDownloadResult result = fileService.downloadFile(id);
// return ResponseEntity.ok()
// .contentType(result.getMediaType())
// .header(HttpHeaders.CONTENT_DISPOSITION, result.getContentDisposition())
// .body(result.getResource());
// }

// @PostMapping("/{id}/file/delete")
// @Operation(summary = "파일 삭제", description = "사용자에게 첨부된 파일을 삭제합니다.")
// public ResponseEntity<ApiResponse<String>> deleteFile(@PathVariable Long id)
// throws IOException {
// fileService.deleteFile(id);
// return ResponseUtil.ok("파일이 성공적으로 삭제되었습니다.", "파일 삭제 성공");
// }
// }
