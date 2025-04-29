package com.example.hipreader.domain.discussion.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.discussion.dto.request.CreateDiscussionRequestDto;
import com.example.hipreader.domain.discussion.dto.request.UpdateDiscussionRequestDto;
import com.example.hipreader.domain.discussion.dto.response.CreateDiscussionResponseDto;
import com.example.hipreader.domain.discussion.dto.response.GetDiscussionResponseDto;
import com.example.hipreader.domain.discussion.dto.response.UpdateDiscussionResponseDto;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.discussion.status.Status;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscussionService {

	private final DiscussionRepository discussionRepository;
	private final UserRepository userRepository;
	private final BookRepository bookRepository;

	@Transactional
	public CreateDiscussionResponseDto createDiscussion(@Valid CreateDiscussionRequestDto requestDto,
		AuthUser authUser) {

		User user = userRepository.findById(authUser.getId()).orElseThrow(
			() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
		);

		Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(
			() -> new IllegalArgumentException("책을 찾을 수 없습니다.")
		);

		Discussion discussion = Discussion.builder()
			.topic(requestDto.getTopic())
			.participants(requestDto.getParticipants())
			.scheduledAt(requestDto.getScheduledAt())
			.status(Status.WAITING)
			.user(user)
			.book(book)
			.mode(requestDto.getMode())
			.build();

		Discussion saved = discussionRepository.save(discussion);

		return CreateDiscussionResponseDto.toDto(saved);
	}

	@Transactional(readOnly = true)
	public Page<GetDiscussionResponseDto> getDiscussions(int page, int size) {
		PageRequest pageRequest = PageRequest.of(Math.max(0, page - 1), size,
			Sort.by(Sort.Direction.DESC, "updatedAt"));

		return discussionRepository.findAll(pageRequest)
			.map(discussion -> new GetDiscussionResponseDto(
				discussion.getId(),
				discussion.getTopic(),
				discussion.getParticipants(),
				discussion.getScheduledAt(),
				discussion.getStatus(),
				discussion.getUser().getId(),
				discussion.getBook().getId()));
	}

	@Transactional(readOnly = true)
	public GetDiscussionResponseDto getDiscussion(Long discussionId) {

		Discussion discussion = discussionRepository.findById(discussionId).orElseThrow(
			() -> new NotFoundException(DISCUSSION_NOT_FOUND)
		);

		return GetDiscussionResponseDto.toDto(discussion);
	}

	@Transactional
	public UpdateDiscussionResponseDto updateDiscussion(AuthUser authUser,
		UpdateDiscussionRequestDto updateDiscussionRequestDto, Long discussionId) {

		Discussion discussion = discussionRepository.findById(discussionId).orElseThrow(
			() -> new NotFoundException(DISCUSSION_NOT_FOUND)
		);

		if (!discussion.getUser().getId().equals(authUser.getId())) {
			throw new NotFoundException(USER_NOT_FOUND);
		}

		if (updateDiscussionRequestDto.getTopic() != null) {
			discussion.updateTopic(updateDiscussionRequestDto.getTopic());
		}

		if (updateDiscussionRequestDto.getParticipants() != null) {
			discussion.updateParticipants(updateDiscussionRequestDto.getParticipants());
		}

		if (updateDiscussionRequestDto.getScheduledAt() != null) {
			discussion.updateScheduledAt(updateDiscussionRequestDto.getScheduledAt());
		}

		if (updateDiscussionRequestDto.getStatus() != null) {
			discussion.updateStatus(updateDiscussionRequestDto.getStatus());
		}

		if (updateDiscussionRequestDto.getMode() != null) {
			discussion.updateMode(updateDiscussionRequestDto.getMode());
		}

		return UpdateDiscussionResponseDto.toDto(discussion);
	}

	@Transactional
	public void deleteDiscussion(AuthUser authUser, Long discussionId) {

		Discussion discussion = discussionRepository.findById(discussionId).orElseThrow(
			() -> new NotFoundException(DISCUSSION_NOT_FOUND)
		);

		if (!discussion.getUser().getId().equals(authUser.getId())) {
			throw new NotFoundException(USER_NOT_FOUND);
		}

		discussionRepository.deleteById(discussion.getId());
	}
}
