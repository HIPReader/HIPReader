package com.example.hipreader.domain.discussion;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.discussion.dto.request.CreateDiscussionRequestDto;
import com.example.hipreader.domain.discussion.dto.response.CreateDiscussionResponseDto;
import com.example.hipreader.domain.discussion.dto.response.GetDiscussionResponseDto;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.discussion.service.DiscussionService;
import com.example.hipreader.domain.discussion.status.Status;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.vo.UserRole;
import com.example.hipreader.domain.userdiscussion.status.DiscussionMode;

@ExtendWith(MockitoExtension.class)
public class DiscussionServiceTest {

	@InjectMocks
	private DiscussionService discussionService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private DiscussionRepository discussionRepository;

	@Test
	void createDiscussion_success() {
		// given
		Long userId = 1L;
		Long bookId = 10L;

		AuthUser authUser = new AuthUser(userId, "user@example.com", UserRole.ROLE_USER);

		CreateDiscussionRequestDto requestDto = CreateDiscussionRequestDto.builder()
			.topic("토론 주제")
			.participants(10)
			.scheduledAt(LocalDateTime.of(2025, 5, 1, 20, 0))
			.bookId(bookId)
			.mode(DiscussionMode.AUTO_APPROVAL)
			.build();

		User mockUser = User.builder()
			.id(userId)
			.nickname("testUser")
			.email("test@example.com")
			.password("password123")
			.age(30)
			.role(UserRole.ROLE_USER)
			.gender(Gender.MALE)
			.build();

		Book mockBook = Book.builder()
			.id(bookId)
			.title("Book Title")
			.isbn13("1234567890123")
			.publisher("Puvlisher")
			.datePublished(LocalDate.of(2023, 1, 1))
			.build();

		Discussion mockDiscussion = Discussion.builder()
			.id(100L)
			.topic(requestDto.getTopic())
			.participants(requestDto.getParticipants())
			.scheduledAt(requestDto.getScheduledAt())
			.status(Status.WAITING)
			.user(mockUser)
			.book(mockBook)
			.mode(requestDto.getMode())
			.build();

		given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
		given(bookRepository.findById(bookId)).willReturn(Optional.of(mockBook));
		given(discussionRepository.save(any(Discussion.class))).willReturn(mockDiscussion);

		// when
		CreateDiscussionResponseDto result = discussionService.createDiscussion(requestDto, authUser);

		// then
		assertThat(result.getTopic()).isEqualTo("토론 주제");
		assertThat(result.getParticipants()).isEqualTo(10);
		assertThat(result.getMode()).isEqualTo(DiscussionMode.AUTO_APPROVAL);
	}

	@Test
	void getDiscussions_success() {
		// given
		int page = 1;
		int size = 10;

		User mockUser = User.builder()
			.id(1L)
			.nickname("testUser")
			.email("test@example.com")
			.password("password123")
			.age(30)
			.role(UserRole.ROLE_USER)
			.gender(Gender.MALE)
			.build();

		Book mockBook = Book.builder()
			.id(10L)
			.title("Book Title")
			.isbn13("1234567890123")
			.publisher("Publisher")
			.datePublished(LocalDate.of(2023, 1, 1))
			.build();

		Discussion mockDiscussion = Discussion.builder()
			.id(100L)
			.topic("토론 주제")
			.participants(10)
			.scheduledAt(LocalDateTime.of(2025, 5, 1, 20, 0))
			.status(Status.WAITING)
			.user(mockUser)
			.book(mockBook)
			.mode(DiscussionMode.AUTO_APPROVAL)
			.build();

		Page<Discussion> mockPage = new PageImpl<>(List.of(mockDiscussion));
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

		given(discussionRepository.findAll(pageable)).willReturn(mockPage);

		// when
		Page<GetDiscussionResponseDto> result = discussionService.getDiscussions(page, size);

		// then
		assertThat(result).hasSize(1);
		GetDiscussionResponseDto dto = result.getContent().get(0);
		assertThat(dto.getTopic()).isEqualTo("토론 주제");
		assertThat(dto.getParticipants()).isEqualTo(10);
		assertThat(dto.getUserId()).isEqualTo(mockUser.getId());
		assertThat(dto.getBookId()).isEqualTo(mockBook.getId());
	}

	@Test
	void getDiscussion_success() {
		// given
		Long discussionId = 100L;

		User mockUser = User.builder()
			.id(1L)
			.nickname("testUser")
			.email("test@example.com")
			.password("password123")
			.age(30)
			.role(UserRole.ROLE_USER)
			.gender(Gender.MALE)
			.build();

		Book mockBook = Book.builder()
			.id(10L)
			.title("Book Title")
			.isbn13("1234567890123")
			.publisher("Publisher")
			.datePublished(LocalDate.of(2023, 1, 1))
			.build();

		Discussion mockDiscussion = Discussion.builder()
			.id(discussionId)
			.topic("단건 조회 토론")
			.participants(10)
			.scheduledAt(LocalDateTime.of(2025, 5, 1, 20, 0))
			.status(Status.WAITING)
			.user(mockUser)
			.book(mockBook)
			.mode(DiscussionMode.AUTO_APPROVAL)
			.build();

		given(discussionRepository.findById(discussionId)).willReturn(Optional.of(mockDiscussion));

		// when
		GetDiscussionResponseDto result = discussionService.getDiscussion(discussionId);

		// then
		assertThat(result.getTopic()).isEqualTo("단건 조회 토론");

	}
}
