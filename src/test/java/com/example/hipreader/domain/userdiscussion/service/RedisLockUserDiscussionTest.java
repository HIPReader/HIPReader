package com.example.hipreader.domain.userdiscussion.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.util.StopWatch;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.repository.DiscussionRepository;
import com.example.hipreader.domain.discussion.status.Status;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.dto.request.ApplyUserDiscussionRequestDto;
import com.example.hipreader.domain.userdiscussion.repository.UserDiscussionRepository;
import com.example.hipreader.domain.userdiscussion.status.DiscussionMode;
import com.example.hipreader.domain.userdiscussion.support.TestResultCollector;

import jakarta.persistence.EntityManagerFactory;

@SpringBootTest
@AutoConfigureMockMvc
@Commit
public class RedisLockUserDiscussionTest {

	@Autowired
	private UserDiscussionService userDiscussionService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DiscussionRepository discussionRepository;

	@Autowired
	private UserDiscussionRepository userDiscussionRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private Discussion discussion;
	private int totalUsers = 1000;
	private static final int MAX_PARTICIPANTS = 10;

	public void createTestDiscussionDate() {
		// 1. 테스트 유저 1000명 생성
		for (int i = 0; i < totalUsers; i++) {
			userRepository.save(User.builder()
				.nickname("user" + i)
				.email("user" + i + "@test.com")
				.password("1234")
				.age(20)
				.role(com.example.hipreader.domain.user.vo.UserRole.ROLE_USER)
				.build());
		}

		// 2. 테스트용 book 생성
		Book book = bookRepository.save(Book.builder()
			.title("테스트용 책")
			.isbn13("1234567890123" + System.nanoTime()) // 중복 방지
			.publisher("테스트출판사")
			.datePublished(LocalDate.of(2023, 1, 1))
			.totalPages(300)
			.build());

		// 3. 테스트용 Discussion 생성 (mode = AUTO_APPROVAL)
		User host = userRepository.findAll().get(0);
		discussion = discussionRepository.save(Discussion.builder()
			.topic("동시성 테스트")
			.participants(MAX_PARTICIPANTS)
			.currentParticipants(0)
			.scheduledAt(LocalDateTime.now().plusDays(1))
			.mode(DiscussionMode.AUTO_APPROVAL)
			.status(Status.WAITING)
			.user(host)
			.book(book)
			.build());
	}

	@BeforeEach
	void setUp() {
		createTestDiscussionDate();

		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		sessionFactory.getStatistics().setStatisticsEnabled(true);
	}

	@DisplayName("[Redis] 자동 참여방에 1000명 동시 신청 시 성공 10명/실패 990명")
	@Test
	void testRedisLock_MultiSize() throws InterruptedException {
		TestResultCollector collector = new TestResultCollector();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		ExecutorService executor = Executors.newFixedThreadPool(500);
		CountDownLatch latch = new CountDownLatch(totalUsers);

		List<Long> successUsers = Collections.synchronizedList(new ArrayList<>());
		List<Long> failedUsers = Collections.synchronizedList(new ArrayList<>());
		List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

		List<User> users = userRepository.findAll();
		final Discussion discussionCopy = this.discussion;

		for (int i = 0; i < totalUsers; i++) {
			final int index = i;
			executor.submit(() -> {
				try {
					AuthUser authUser = new AuthUser(users.get(index).getId(), users.get(index).getEmail(),
						users.get(index).getRole());
					ApplyUserDiscussionRequestDto dto = new ApplyUserDiscussionRequestDto(discussionCopy.getId());

					Thread.sleep(30);
					synchronized (UserDiscussionService.class) {
						userDiscussionService.autoApply(authUser, dto);
					}

					successUsers.add(authUser.getId());
				} catch (Exception e) {
					failedUsers.add(users.get(index).getId());
					exceptions.add(e);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();
		stopWatch.stop();

		collector.record(
			stopWatch.getTotalTimeMillis(),
			successUsers.size(),
			failedUsers.size(),
			exceptions.size(),
			0
		);
		System.out.println("[Redis Lock 테스트 결과]");
		System.out.println(collector);

		assertThat(collector.getSuccessCount()).isEqualTo(MAX_PARTICIPANTS);
		assertThat(collector.getFailureCount()).isEqualTo(totalUsers - MAX_PARTICIPANTS);

		long savedCount =
			userDiscussionRepository.countByDiscussionAndStatus(discussionCopy, ApplicationStatus.APPROVED);

		assertThat(savedCount).isEqualTo(MAX_PARTICIPANTS);

		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Statistics stats = sessionFactory.getStatistics();

		System.out.println("[Hibernate 통계]");
		System.out.println("총 실행 쿼리 수: " + stats.getQueryExecutionCount());
		System.out.println("총 트랜잭션 수: " + stats.getTransactionCount());
		System.out.println("성공한 트랜잭션 수: " + stats.getSuccessfulTransactionCount());
		System.out.println("플러시 수(flush 호출 수): " + stats.getFlushCount());
		System.out.println("엔티티 insert 수: " + stats.getEntityInsertCount());
		System.out.println("엔티티 update 수: " + stats.getEntityUpdateCount());
		System.out.println("엔티티 delete 수: " + stats.getEntityDeleteCount());
	}
}
