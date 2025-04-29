package com.example.hipreader.domain.userdiscussion.service;

import java.util.List;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.userdiscussion.dto.request.ApplyUserDiscussionRequestDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApplyUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApproveUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.GetUserAppliedDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.GetUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.RejectUserDiscussionResponseDto;

public interface UserDiscussionService {
	ApplyUserDiscussionResponseDto apply(AuthUser authUser, ApplyUserDiscussionRequestDto requestDto);
	ApproveUserDiscussionResponseDto approve(AuthUser authUser, Long userDiscussionId);
	RejectUserDiscussionResponseDto reject(AuthUser authUser, Long userDiscussionId);
	List<GetUserDiscussionResponseDto> findByDiscussion(Long discussionId);
	List<GetUserAppliedDiscussionResponseDto> findByUser(Long userId);
}
