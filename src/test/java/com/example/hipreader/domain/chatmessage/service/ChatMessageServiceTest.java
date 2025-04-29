package com.example.hipreader.domain.chatmessage.service;

import static org.assertj.core.api.Assertions.*;
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

import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.ErrorCode;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.chatmessage.dto.response.GetChatMessageResponseDto;
import com.example.hipreader.domain.chatmessage.entity.ChatMessage;
import com.example.hipreader.domain.chatmessage.repository.ChatMessageRepository;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.discussion.status.Status;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.vo.UserRole;
import com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.repository.UserDiscussionRepository;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

	@Mock
	ChatMessageRepository chatMessageRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	DiscussionRepository discussionRepository;

	@Mock
	UserDiscussionRepository userDiscussionRepository;

	@InjectMocks
	ChatMessageService chatMessageService;

	@Test
	void 메시지_저장_성공() {
		// given
		User mockUser = User.builder()
			.id(1L)
			.age(20)
			.email("ijieun@gmail.com")
			.nickname("이지은")
			.gender(Gender.FEMALE)
			.password("Password123@")
			.role(UserRole.ROLE_USER)
			.build();

		Book mockBook = Book.builder()
			.id(1L)
			.categoryName("소설")
			.title("타이틀")
			.coverImage(null)
			.datePublished(LocalDate.now())
			.isbn13("12345")
			.publisher("출판사")
			.totalPages(100)
			.build();

		Discussion mockDiscussion = Discussion.builder()
			.id(1L)
			.status(Status.ACTIVE)
			.topic("소설을 제대로 감상하는 방법")
			.participants(5)
			.scheduledAt(LocalDateTime.now())
			.user(mockUser)
			.book(mockBook)
			.build();

		ChatMessage savedMessage = ChatMessage.builder()
			.id(1L)
			.message("안녕하세요!")
			.createdAt(LocalDateTime.now())
			.user(mockUser)
			.discussion(mockDiscussion)
			.build();

		given(discussionRepository.findById(any())).willReturn(Optional.of(mockDiscussion));
		given(userRepository.findById(any())).willReturn(Optional.of(mockUser));
		given(chatMessageRepository.save(any())).willReturn(savedMessage);

		// when
		chatMessageService.saveMessage(1L, 1L, "안녕하세요!");

		// then
		verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
	}

	@Test
	void 메시지_조회_성공() {
		// given
		User mockUser = User.builder()
			.id(1L)
			.age(20)
			.email("ijieun@gmail.com")
			.nickname("이지은")
			.gender(Gender.FEMALE)
			.password("Password123@")
			.role(UserRole.ROLE_USER)
			.build();

		Book mockBook = Book.builder()
			.id(1L)
			.categoryName("소설")
			.title("타이틀")
			.coverImage(null)
			.datePublished(LocalDate.now())
			.isbn13("12345")
			.publisher("출판사")
			.totalPages(100)
			.build();

		Discussion mockDiscussion = Discussion.builder()
			.id(1L)
			.status(Status.ACTIVE)
			.topic("소설을 제대로 감상하는 방법")
			.participants(5)
			.scheduledAt(LocalDateTime.now())
			.user(mockUser)
			.book(mockBook)
			.build();

		List<ChatMessage> chatMessageList = List.of(
			new ChatMessage(1L, mockDiscussion, mockUser, "안녕하세요!", LocalDateTime.now()),
			new ChatMessage(2L, mockDiscussion, mockUser, "반가워요!", LocalDateTime.now()),
			new ChatMessage(3L, mockDiscussion, mockUser, "왜 아무도 대꾸 안해주나요!", LocalDateTime.now())
		);

		given(chatMessageRepository.findByDiscussionIdOrderByCreatedAtAsc(any())).willReturn(chatMessageList);

		// when
		List<GetChatMessageResponseDto> responseDtoList = chatMessageService.getChatMessageHistory(1L);

		// then
		assertThat(responseDtoList.get(0).getMessage()).isEqualTo("안녕하세요!");
		assertThat(responseDtoList.get(1).getMessage()).isEqualTo("반가워요!");
		assertThat(responseDtoList.get(2).getMessage()).isEqualTo("왜 아무도 대꾸 안해주나요!");
	}

	@Test
	void 토론방_입장_자격_검증_성공() {
		// given
		User mockUser = User.builder()
			.id(1L)
			.age(20)
			.email("ijieun@gmail.com")
			.nickname("이지은")
			.gender(Gender.FEMALE)
			.password("Password123@")
			.role(UserRole.ROLE_USER)
			.build();

		Book mockBook = Book.builder()
			.id(1L)
			.categoryName("소설")
			.title("타이틀")
			.coverImage(null)
			.datePublished(LocalDate.now())
			.isbn13("12345")
			.publisher("출판사")
			.totalPages(100)
			.build();

		Discussion mockDiscussion = Discussion.builder()
			.id(1L)
			.status(Status.ACTIVE)
			.topic("소설을 제대로 감상하는 방법")
			.participants(5)
			.scheduledAt(LocalDateTime.now())
			.user(mockUser)
			.book(mockBook)
			.build();

		given(discussionRepository.findById(any())).willReturn(Optional.of(mockDiscussion));
		given(userDiscussionRepository.existsByUserIdAndDiscussionIdAndStatus(mockUser.getId(), mockDiscussion.getId(),
			ApplicationStatus.APPROVED)).willReturn(true);

		// when & then
		assertThatCode(() -> chatMessageService.validateUserCanJoinDiscussion(mockUser.getId(),
			mockDiscussion.getId())).doesNotThrowAnyException();
	}

	@Test
	void 토론방_Status_ACTIVE_아닌_경우_예외처리() {
		// given
		Discussion mockDiscussion = mock(Discussion.class);
		when(mockDiscussion.getStatus()).thenReturn(Status.CLOSED);

		when(discussionRepository.findById(any())).thenReturn(Optional.of(mockDiscussion));

		// when & then
		assertThatThrownBy(() -> chatMessageService.validateUserCanJoinDiscussion(1L, 1L))
			.isInstanceOf(BadRequestException.class)
			.hasMessageContaining(ErrorCode.DISCUSSION_NOT_ACTIVE.getMessage());
	}

	@Test
	void 토론방_입장_APPROVED_안된_경우_예외처리() {
		// given
		Discussion mockDiscussion = mock(Discussion.class);
		when(mockDiscussion.getStatus()).thenReturn(Status.ACTIVE);

		when(discussionRepository.findById(any())).thenReturn(Optional.of(mockDiscussion));
		when(userDiscussionRepository.existsByUserIdAndDiscussionIdAndStatus(1L, 1L,
			ApplicationStatus.APPROVED)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> chatMessageService.validateUserCanJoinDiscussion(1L, 1L))
			.isInstanceOf(UnauthorizedException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_APPROVED.getMessage());
	}
}
