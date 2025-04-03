package com.example.hipreader.common.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponseDto {

    private String url;
    private String size;
    private LocalDateTime createdAt;

    public static UploadResponseDto from(String url, long byteSize) {
        return UploadResponseDto.builder()
                .url(url)
                .size(humanReadableByteCount(byteSize))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + "B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "B";

        return String.format("%.1f %s", bytes / Math.pow(unit, exp), pre);
    }

}
