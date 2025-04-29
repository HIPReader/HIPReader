package com.example.hipreader.domain.userdiscussion.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.dto.request.ApplyUserDiscussionRequestDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApplyUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApproveUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.GetUserAppliedDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.GetUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.NotificationMessage;
import com.example.hipreader.domain.userdiscussion.dto.response.RejectUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;
import com.example.hipreader.domain.userdiscussion.producer.NotificationProducer;
import com.example.hipreader.domain.userdiscussion.repository.UserDiscussionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDiscussionServiceImpl implements UserDiscussionService {

	private final UserDiscussionRepository userDiscussionRepository;
	private final UserRepository userRepository;
	private final DiscussionRepository discussionRepository;
	private final NotificationProducer notificationProducer;

	@Override
	@Transactional
	public ApplyUserDiscussionResponseDto apply(AuthUser authUser, ApplyUserDiscussionRequestDto requestDto) {
		User user = userRepository.findById(authUser.getId())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		Discussion discussion = discussionRepository.findById(requestDto.getDiscussionId())
			.orElseThrow(() -> new NotFoundException(DISCUSSION_NOT_FOUND));

		//중복 신청 방지
		if (userDiscussionRepository.existsByUserAndDiscussion(user, discussion)) {
			throw new BadRequestException(ALREADY_APPLIED);
		}

		UserDiscussion userDiscussion = UserDiscussion.builder()
			.user(user)
			.discussion(discussion)
			.appliedAt(LocalDateTime.now())
			.status(ApplicationStatus.PENDING)
			.build();
		userDiscussionRepository.save(userDiscussion);

		// 알림 발송
		notificationProducer.sendNotification(
			new NotificationMessage(
				userDiscussion.getUser().getId(),
				userDiscussion.getDiscussion().getId(),
				"PENDING",
				LocalDateTime.now()
			)
		);

		return ApplyUserDiscussionResponseDto.toDto(userDiscussion);
	}

	@Override
	@Transactional
	public ApproveUserDiscussionResponseDto approve(AuthUser authUser, Long userDiscussionId) {
		UserDiscussion userDiscussion = userDiscussionRepository.findById(userDiscussionId)
			.orElseThrow(() -> new NotFoundException(APPLICATION_NOT_FOUND));

		if (!userDiscussion.getDiscussion().getUser().getId().equals(authUser.getId())) {
			throw new UnauthorizedException(ONLY_CAN_HOST);
		}
		userDiscussion.setStatus(ApplicationStatus.APPROVED);
		userDiscussion.setStatusUpdatedAt(LocalDateTime.now());

		// 알림 발송
		notificationProducer.sendNotification(
			new NotificationMessage(
				userDiscussion.getUser().getId(),
				userDiscussion.getDiscussion().getId(),
				"APPROVED",
				LocalDateTime.now()
			)
		);

		return ApproveUserDiscussionResponseDto.toDto(userDiscussion);
	}

	@Override
	@Transactional
	public RejectUserDiscussionResponseDto reject(AuthUser authUser, Long userDiscussionId) {
		UserDiscussion userDiscussion = userDiscussionRepository.findById(userDiscussionId)
			.orElseThrow(() -> new NotFoundException(APPLICATION_NOT_FOUND));
		userDiscussion.setStatus(ApplicationStatus.REJECTED);
		userDiscussion.setStatusUpdatedAt(LocalDateTime.now());

		if (!userDiscussion.getDiscussion().getUser().getId().equals(authUser.getId())) {
			throw new UnauthorizedException(ONLY_CAN_HOST);
		}

		//알림발송
		notificationProducer.sendNotification(
			new NotificationMessage(
				userDiscussion.getUser().getId(),
				userDiscussion.getDiscussion().getId(),
				"REJECTED",
				LocalDateTime.now()
			)
		);

		return RejectUserDiscussionResponseDto.toDto(userDiscussion);
	}

	@Override
	public List<GetUserDiscussionResponseDto> findByDiscussion(Long discussionId) {
		Discussion discussion = discussionRepository.findById(discussionId)
			.orElseThrow(() -> new NotFoundException(DISCUSSION_NOT_FOUND));
		// 1. UserDiscussion 리스트 조회
		List<UserDiscussion> userDiscussions = userDiscussionRepository.findByDiscussionWithUser(discussion);

		// 2. DTO 변환 및 반환
		return userDiscussions.stream()
			.map(GetUserDiscussionResponseDto::toDto)
			.toList();
	}

	@Override
	public List<GetUserAppliedDiscussionResponseDto> findByUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
		List<UserDiscussion> userDiscussions = userDiscussionRepository.findByUserWithDiscussion(user);
		return userDiscussions.stream()
			.map(GetUserAppliedDiscussionResponseDto::toDto)
			.toList();
	}
}
