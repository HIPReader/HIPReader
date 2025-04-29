package com.example.hipreader.domain.userdiscussion.service;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.discussion.status.Status;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.UserRole;
import com.example.hipreader.domain.userdiscussion.repository.UserDiscussionRepository;
import com.example.hipreader.domain.userdiscussion.status.DiscussionMode;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class TestDataInitializer {

	private final UserRepository userRepository;
	private final BookRepository bookRepository;
	private final DiscussionRepository discussionRepository;
	private final EntityManager em;
	private final UserDiscussionRepository userDiscussionRepository;

	public TestDataInitializer(
		UserRepository userRepository,
		BookRepository bookRepository,
		DiscussionRepository discussionRepository,
		EntityManager em,
		UserDiscussionRepository userDiscussionRepository) {
		this.userRepository = userRepository;
		this.bookRepository = bookRepository;
		this.discussionRepository = discussionRepository;
		this.em = em;
		this.userDiscussionRepository = userDiscussionRepository;
	}

	private int totalUsers = 100;
	private static final int MAX_PARTICIPANTS = 10;

	public void setTotalUsers(int totalUsers) {
		this.totalUsers = totalUsers;
	}

	@Transactional
	public Discussion createTestDiscussionData() {
		// 1. 테스트 유저 생성
		IntStream.range(0, totalUsers).forEach(i -> {
			userRepository.save(User.builder()
				.nickname("user" + i)
				.email("user" + i + "@test.com")
				.password("1234")
				.age(20)
				.role(UserRole.ROLE_USER)
				.build());
		});

		// 2. 테스트용 book 생성
		Book book = bookRepository.save(Book.builder()
			.title("테스트용 책")
			.isbn13("1234567890123" + System.nanoTime()) // 중복 방지
			.publisher("테스트출판사")
			.datePublished(java.time.LocalDate.of(2023, 1, 1))
			.totalPages(300)
			.build());

		// 3. 테스트용 Discussion 생성 (mode = AUTO_APPROVAL)
		User host = userRepository.findAll().get(0);
		return discussionRepository.save(Discussion.builder()
			.topic("동시성 테스트")
			.participants(MAX_PARTICIPANTS)
			.scheduledAt(LocalDateTime.now().plusDays(1))
			.mode(DiscussionMode.AUTO_APPROVAL)
			.status(Status.WAITING)
			.user(host)
			.book(book)
			.build());
	}
}
