package com.example.hipreader.domain.discussion.service;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.discussion.dto.request.DiscussionCreateRequestDto;
import com.example.hipreader.domain.discussion.dto.response.DiscussionCreateResponseDto;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.discussion.status.Status;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public DiscussionCreateResponseDto createDiscussion(@Valid DiscussionCreateRequestDto requestDto, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
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
                .build();

        Discussion saved = discussionRepository.save(discussion);

        return DiscussionCreateResponseDto.builder()
                .id(saved.getId())
                .topic(saved.getTopic())
                .scheduledAt(saved.getScheduledAt())
                .participants(saved.getParticipants())
                .status(saved.getStatus())
                .hostId(user.getId())
                .hostName(user.getNickname())
                .message("토론방이 성공적으로 생성되었습니다.")
                .build();
    }
}
