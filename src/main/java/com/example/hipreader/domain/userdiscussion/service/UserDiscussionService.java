package com.example.hipreader.domain.userdiscussion.service;

import java.util.List;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.userdiscussion.dto.request.ApplyUserDiscussionRequestDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApplyUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApproveUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.RejectUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;

public interface UserDiscussionService {
	ApplyUserDiscussionResponseDto apply(AuthUser authUser, ApplyUserDiscussionRequestDto requestDto);

	ApplyUserDiscussionResponseDto autoApply(AuthUser authUser, ApplyUserDiscussionRequestDto requestDto);

	ApproveUserDiscussionResponseDto approve(Long userDiscussionId);

	RejectUserDiscussionResponseDto reject(Long userDiscussionId);

	List<UserDiscussion> findByDiscussion(Long discussionId);

	List<UserDiscussion> findByUser(Long userId);
}
