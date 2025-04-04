package com.example.hipreader.domain.userbook.dto.request;

import com.example.hipreader.domain.userbook.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterUserBookRequestDto {
    private Long bookId;
    private Status status;
}
