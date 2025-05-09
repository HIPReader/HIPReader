package com.example.hipreader.domain.userdiscussion.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
import com.example.hipreader.domain.userdiscussion.status.DiscussionMode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDiscussionServiceImpl implements UserDiscussionService {

	private final UserDiscussionRepository userDiscussionRepository;
	private final UserRepository userRepository;
	private final DiscussionRepository discussionRepository;
	private final NotificationProducer notificationProducer;
	private final RedissonClient redissonClient;

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
	public ApplyUserDiscussionResponseDto autoApply(AuthUser authUser, ApplyUserDiscussionRequestDto requestDto) {
		// 1. 요청한 토론방 조회
		Discussion discussion = discussionRepository.findById(requestDto.getDiscussionId()).orElseThrow(
			() -> new NotFoundException(DISCUSSION_NOT_FOUND)
		);

		// 2. 자동 참여 방인지 확인
		if (discussion.getMode() != DiscussionMode.AUTO_APPROVAL) {
			throw new BadRequestException(INVALID_REQUEST);
		}

		// 3. Redis 분산 락 획득 시도
		String lockKey = "discussion:lock:" + discussion.getId();
		RLock lock = redissonClient.getLock(lockKey);

		try {
			// 4. 락을 획득하지 못하면 에러
			if (!lock.tryLock(10, TimeUnit.SECONDS)) {
				throw new RuntimeException("참여 요청이 몰리고 있습니다. 잠시 후 다시 시도해주세요.");
			}

			// 5. 유저가 이미 신청했는지 확인
			boolean alreadyJoined =
				userDiscussionRepository.existsByUserIdAndDiscussionId(authUser.getId(), discussion.getId());
			if (alreadyJoined) {
				throw new BadRequestException(ALREADY_APPLIED);
			}

			// 6. 현재 참여 승인된 인원 수 조회

			// 7. 유저 정보 조회
			User user = userRepository.findById(authUser.getId()).orElseThrow(
				() -> new NotFoundException(USER_NOT_FOUND)
			);

			// 8. 참여 정보 생성
			UserDiscussion userDiscussion = UserDiscussion.builder()
				.discussion(discussion)
				.user(user)
				.status(ApplicationStatus.APPROVED)
				.appliedAt(LocalDateTime.now())
				.statusUpdatedAt(LocalDateTime.now())
				.build();

			// 9. DB 저장 전 정원 확인
			long acceptedCount =
				userDiscussionRepository.countByDiscussionAndStatus(discussion, ApplicationStatus.APPROVED);
			if (acceptedCount >= discussion.getParticipants()) {
				throw new BadRequestException(DISCUSSION_FULL);
			}

			// 10. DB 저장
			userDiscussionRepository.saveAndFlush(userDiscussion);

			long confirmedCount =
				userDiscussionRepository.countByDiscussionAndStatus(discussion, ApplicationStatus.APPROVED);
			if (confirmedCount > discussion.getParticipants()) {
				throw new BadRequestException(DISCUSSION_FULL);
			}

			// // 11. 알림 전송
			// notificationProducer.sendNotification(
			// 	new NotificationMessage(
			// 		userDiscussion.getUser().getId(),
			// 		userDiscussion.getDiscussion().getId(),
			// 		"APPROVED",
			// 		LocalDateTime.now()
			// 	)
			// );

			// 12. 응답 DTO 반환
			return ApplyUserDiscussionResponseDto.toDto(userDiscussion);

		} catch (InterruptedException e) {
			throw new RuntimeException("락 처리 중 오류가 발생했습니다.");
		} finally {
			// 13. 락 해제
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	@Transactional
	public ApplyUserDiscussionResponseDto applyWithPessimisticLock(AuthUser authUser,
		ApplyUserDiscussionRequestDto requestDto) {
		Discussion discussion = discussionRepository.findByIdWithPessimisticLock(requestDto.getDiscussionId())
			.orElseThrow(() -> new NotFoundException(DISCUSSION_NOT_FOUND));

		if (discussion.getMode() != DiscussionMode.AUTO_APPROVAL) {
			throw new BadRequestException(INVALID_REQUEST);
		}

		if (userDiscussionRepository.existsByUserIdAndDiscussionId(authUser.getId(), discussion.getId())) {
			throw new BadRequestException(ALREADY_APPLIED);
		}

		long acceptedCount = userDiscussionRepository.countByDiscussionAndStatus(discussion,
			ApplicationStatus.APPROVED);
		if (acceptedCount >= discussion.getParticipants()) {
			throw new BadRequestException(DISCUSSION_FULL);
		}

		discussion.increaseCurrentParticipants();
		discussionRepository.saveAndFlush(discussion);

		User user = userRepository.findById(authUser.getId())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		UserDiscussion userDiscussion = UserDiscussion.builder()
			.discussion(discussion)
			.user(user)
			.status(ApplicationStatus.APPROVED)
			.appliedAt(LocalDateTime.now())
			.statusUpdatedAt(LocalDateTime.now())
			.build();

		userDiscussionRepository.save(userDiscussion);
		return ApplyUserDiscussionResponseDto.toDto(userDiscussion);
	}

	@Transactional
	public ApplyUserDiscussionResponseDto applyWithOptimisticLock(AuthUser authUser,
		ApplyUserDiscussionRequestDto requestDto) {

		// 1. Discussion 조회
		Discussion discussion = discussionRepository.findById(requestDto.getDiscussionId())
			.orElseThrow(() -> new NotFoundException(DISCUSSION_NOT_FOUND));

		// 2. 자동 참여 방인지 확인
		if (discussion.getMode() != DiscussionMode.AUTO_APPROVAL) {
			throw new BadRequestException(INVALID_REQUEST);
		}

		// 3. 중복 신청 여부 확인
		if (userDiscussionRepository.existsByUserIdAndDiscussionId(authUser.getId(), discussion.getId())) {
			throw new BadRequestException(ALREADY_APPLIED);
		}

		// 4. 현재 인원 확인
		long acceptedCount = userDiscussionRepository.countByDiscussionAndStatus(discussion,
			ApplicationStatus.APPROVED);
		if (acceptedCount >= discussion.getParticipants()) {
			throw new BadRequestException(DISCUSSION_FULL);
		}

		try {
			// 5. 현재 참여자 수 증가
			discussion.increaseCurrentParticipants();
			// 6. Discussion 저장(버전 증가 발생)
			discussionRepository.saveAndFlush(discussion);
		} catch (ObjectOptimisticLockingFailureException e) {
			throw new ObjectOptimisticLockingFailureException(Discussion.class, discussion.getId(), e);
		}

		//7. 유저 조회
		User user = userRepository.findById(authUser.getId())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		// 8. UserDiscussion 생성
		UserDiscussion userDiscussion = UserDiscussion.builder()
			.discussion(discussion)
			.user(user)
			.status(ApplicationStatus.APPROVED)
			.appliedAt(LocalDateTime.now())
			.build();

		// 9. UserDiscussion 저장 (별도 버전 X)
		userDiscussionRepository.save(userDiscussion);

		// // 10. 최종 인원 초과 확인(예외를 막기 위한 double-check)
		long confirmedCount = userDiscussionRepository.countByDiscussionAndStatus(discussion,
			ApplicationStatus.APPROVED);
		if (confirmedCount > discussion.getParticipants()) {
			throw new BadRequestException(DISCUSSION_FULL);
		}

		// 11. 응답 반환
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
	public List<GetUserAppliedDiscussionResponseDto> findByUser(AuthUser authUser) {
		User user = userRepository.findById(authUser.getId())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
		List<UserDiscussion> userDiscussions = userDiscussionRepository.findByUserWithDiscussion(user);
		return userDiscussions.stream()
			.map(GetUserAppliedDiscussionResponseDto::toDto)
			.toList();
	}
}
