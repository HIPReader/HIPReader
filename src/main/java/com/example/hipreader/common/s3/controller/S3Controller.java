package com.example.hipreader.common.s3.controller;

import com.example.hipreader.common.s3.dto.MultipleUploadResponseDto;
import com.example.hipreader.common.s3.dto.UploadResponseDto;
import com.example.hipreader.common.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;

    // 단일 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<UploadResponseDto> upload(@RequestParam("file") MultipartFile file) throws IOException {
        UploadResponseDto dto = s3Service.uploadFile(file);

        return ResponseEntity.ok(dto);
    }

    // 다중 파일 업로드
    @PostMapping("/multiple/upload")
    public ResponseEntity<MultipleUploadResponseDto> uploadMultiple(@RequestPart("files") List<MultipartFile> files) throws IOException {
        MultipleUploadResponseDto response = s3Service.uploadFiles(files);
        return ResponseEntity.ok(response);
    }
}
