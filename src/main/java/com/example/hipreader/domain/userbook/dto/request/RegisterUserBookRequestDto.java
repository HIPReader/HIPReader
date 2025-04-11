package com.example.hipreader.domain.userbook.dto.request;

import com.example.hipreader.domain.userbook.status.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterUserBookRequestDto {
    @NotBlank
    private Long bookId;
    @NotBlank
    private Status status;
    private Integer totalPages;
}
