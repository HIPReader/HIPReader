package com.example.hipreader.domain.userbook.dto.request;

import com.example.hipreader.domain.userbook.status.Status;
import lombok.Getter;

@Getter
public class UpdateUserBookRequestDto {
    private Status status;
    private int progress;
}
