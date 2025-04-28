package com.example.hipreader.domain.chatmessage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.ErrorCode;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.domain.chatmessage.dto.response.GetChatMessageResponseDto;
import com.example.hipreader.domain.chatmessage.entity.ChatMessage;
import com.example.hipreader.domain.chatmessage.repository.ChatMessageRepository;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.discussion.status.Status;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.repository.UserDiscussionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;
	private final UserRepository userRepository;
	private final DiscussionRepository discussionRepository;
	private final UserDiscussionRepository userDiscussionRepository;

	// 메시지 저장
	public void saveMessage(Long discussionId, Long userId, String message) {
		Discussion discussion = discussionRepository.findById(discussionId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.DISCUSSION_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		ChatMessage chatMessage = ChatMessage.builder()
			.message(message)
			.discussion(discussion)
			.user(user)
			.createdAt(LocalDateTime.now())
			.build();

		chatMessageRepository.save(chatMessage);
	}

	// 메시지 조회
	public List<GetChatMessageResponseDto> getChatMessageHistory(Long discussionId) {
		List<ChatMessage> chatMessageList = chatMessageRepository.findByDiscussionIdOrderByCreatedAtAsc(
			discussionId);
		return chatMessageList.stream()
			.map(GetChatMessageResponseDto::toDto)
			.collect(Collectors.toList());
	}

	// 토론방 입장 가능한지 검증
	public void validateUserCanJoinDiscussion(Long userId, Long discussionId) {
		// discussion 존재 여부 확인
		Discussion discussion = discussionRepository.findById(discussionId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.DISCUSSION_NOT_FOUND));

		// discussion status 가 ACTIVE 인지 확인
		if (!discussion.getStatus().equals(Status.ACTIVE)) {
			throw new BadRequestException(ErrorCode.DISCUSSION_NOT_ACTIVE);
		}

		// userdiscussion 에서 APPROVED 여부 확인
		boolean isApproved = userDiscussionRepository.existsByUserIdAndDiscussionIdAndStatus(userId, discussionId,
			ApplicationStatus.APPROVED);

		if (!isApproved)
			throw new UnauthorizedException(ErrorCode.USER_NOT_APPROVED);
	}
}
