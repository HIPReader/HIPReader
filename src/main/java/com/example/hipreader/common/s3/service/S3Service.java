package com.example.hipreader.common.s3.service;


import com.example.hipreader.common.s3.dto.MultipleUploadResponseDto;
import com.example.hipreader.common.s3.dto.UploadResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public UploadResponseDto uploadFile(MultipartFile file) throws IOException {
        String fileName = String.format("%s-%s",UUID.randomUUID(), file.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return UploadResponseDto.from(
                String.format("https://%s.s3.amazonaws.com/%s", bucket, fileName),
                file.getSize()
        );
    }

    public MultipleUploadResponseDto uploadFiles(List<MultipartFile> files) throws IOException {
        List<UploadResponseDto> uploadResponseDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            uploadResponseDtos.add(uploadFile(file));
        }

        return MultipleUploadResponseDto.builder()
                .files(uploadResponseDtos)
                .build();
    }
}
