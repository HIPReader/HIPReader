package com.example.hipreader.domain.discussion.dto.response;

import com.example.hipreader.domain.discussion.status.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionCreateResponseDto {

    private Long id;
    private String topic;
    private LocalDateTime scheduledAt;
    private Integer participants;
    private Status status;
    private String message;

    private Long hostId;
    private String hostName;
}
