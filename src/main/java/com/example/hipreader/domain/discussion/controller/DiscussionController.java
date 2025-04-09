package com.example.hipreader.domain.discussion.controller;

import com.example.hipreader.domain.discussion.dto.request.DiscussionCreateRequestDto;
import com.example.hipreader.domain.discussion.dto.response.DiscussionCreateResponseDto;
import com.example.hipreader.domain.discussion.service.DiscussionService;
import com.example.hipreader.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;

    @PostMapping
    public ResponseEntity<DiscussionCreateResponseDto> createDiscussion(
            @RequestBody @Valid DiscussionCreateRequestDto requestDto,
            @AuthenticationPrincipal User user
            ) {
        return ResponseEntity.ok(discussionService.createDiscussion(requestDto, user.getId()));
    }
}
