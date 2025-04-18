package com.example.hipreader.domain.userdiscussion.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userdiscussion.ApplicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.dto.request.ApplyUserDiscussionRequestDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApplyUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApproveUserDiscussionResponseDto;
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

	@Override
	@Transactional
	public ApproveUserDiscussionResponseDto approve(Long userDiscussionId) {
		UserDiscussion userDiscussion = userDiscussionRepository.findById(userDiscussionId)
			.orElseThrow(() -> new NotFoundException(APPLICATION_NOT_FOUND));
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
	public RejectUserDiscussionResponseDto reject(Long userDiscussionId) {
		UserDiscussion userDiscussion = userDiscussionRepository.findById(userDiscussionId)
			.orElseThrow(() -> new NotFoundException(APPLICATION_NOT_FOUND));
		userDiscussion.setStatus(ApplicationStatus.REJECTED);
		userDiscussion.setStatusUpdatedAt(LocalDateTime.now());

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
	public List<UserDiscussion> findByDiscussion(Long discussionId) {
		Discussion discussion = discussionRepository.findById(discussionId)
			.orElseThrow(() -> new NotFoundException(DISCUSSION_NOT_FOUND));

		return userDiscussionRepository.findByDiscussion(discussion);
	}

	@Override
	public List<UserDiscussion> findByUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		return userDiscussionRepository.findByUser(user);
	}
}
