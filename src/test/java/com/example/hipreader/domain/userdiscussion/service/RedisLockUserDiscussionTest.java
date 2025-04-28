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
import com.example.hipreader.domain.user.role.UserRole;
import com.example.hipreader.domain.userdiscussion.ApplicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.dto.request.ApplyUserDiscussionRequestDto;
import com.example.hipreader.domain.userdiscussion.repository.UserDiscussionRepository;
import com.example.hipreader.domain.userdiscussion.status.DiscussionMode;

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

	private Discussion discussion;
	private static final int TOTAL_USERS = 100;
	private static final int MAX_PARTICIPANTS = 10;

	public void createTestDiscussionDate() {
		// 1. 테스트 유저 100명 생성
		for (int i = 0; i < totalUsers; i++) {
			userRepository.save(User.builder()
				.nickname("user" + i)
				.email("user" + i + "@test.com")
				.password("1234")
				.age(20)
				.role(UserRole.ROLE_USER)
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
	}

	@DisplayName("자동 참여방에 100명 동시 신청 시 성공 10명/실패 90명")
	@Test
	void testConcurrencyAutoApply() throws InterruptedException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		ExecutorService executor = Executors.newFixedThreadPool(20);
		CountDownLatch latch = new CountDownLatch(totalUsers);

		List<Long> successUsers = Collections.synchronizedList(new ArrayList<>());
		List<Long> failedUsers = Collections.synchronizedList(new ArrayList<>());
		List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

		List<User> users = userRepository.findAll();
		final Discussion discussionCopy = this.discussion;

		System.out.println("생성된 discussion ID: " + discussionCopy.getId());

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
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		stopWatch.stop();

		System.out.println("[Redis Lock 테스트 결과]");
		System.out.println("소요 시간: " + stopWatch.getTotalTimeMillis() + "ms");
		System.out.println("성공 수: " + successUsers.size() + "명");
		System.out.println("실패 수: " + failedUsers.size() + "명");
		System.out.println("예외 발생 수: " + exceptions.size() + "건");

		assertThat(successUsers.size()).isEqualTo(MAX_PARTICIPANTS);
		assertThat(failedUsers.size()).isEqualTo(totalUsers - MAX_PARTICIPANTS);

		long savedCount =
			userDiscussionRepository.countByDiscussionAndStatus(discussionCopy, ApplicationStatus.APPROVED);

		assertThat(savedCount).isEqualTo(MAX_PARTICIPANTS);
	}

	@Transactional
	@ParameterizedTest
	@ValueSource(ints = {100, 500, 1000})
	@DisplayName("[Reids Lock] 다양한 신청 인원에 대한 성능 테스트 (10회 반복)")
	void testRedisLock_MultiSize(int totalUsers) throws InterruptedException, IOException {
		long totalElapsedTime = 0;
		int totalSuccess = 0;
		int totalFailure = 0;
		int totalException = 0;

		StringBuilder resultLog = new StringBuilder();
		resultLog.append("==== 신청자 수: ").append(totalUsers).append("명 테스트 시작 ====\n");

		for (int i = 1; i <= 10; i++) {
			System.out.println("반복 회차: " + i + " (신청자 수: " + totalUsers + ")");

			// 데이터 초기화
			userDiscussionRepository.deleteAll();
			discussionRepository.deleteAll();
			bookRepository.deleteAll();
			userRepository.deleteAll();
			em.flush();

			this.totalUsers = totalUsers;
			createTestDiscussionDate();

			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			ExecutorService executor = Executors.newFixedThreadPool(20);
			CountDownLatch latch = new CountDownLatch(totalUsers);

			List<Long> successUsers = Collections.synchronizedList(new ArrayList<>());
			List<Long> failedUsers = Collections.synchronizedList(new ArrayList<>());
			List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

			List<User> users = userRepository.findAll();
			final Discussion discussionCopy = this.discussion;

			for (int j = 0; j < totalUsers; j++) {
				final int index = j;
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
						e.printStackTrace();
					} finally {
						latch.countDown();
					}
				});
			}

			latch.await();
			executor.shutdown();
			stopWatch.stop();

			long elapsed = stopWatch.getTotalTimeMillis();
			int success = successUsers.size();
			int failure = failedUsers.size();
			int exception = exceptions.size();

			resultLog.append("회차 ").append(i)
				.append(" - 소요 시간: ").append(elapsed).append("ms")
				.append(", 성공: ").append(success)
				.append(", 실패: ").append(failure)
				.append(", 에외: ").append(exception).append("\n");

			totalElapsedTime += elapsed;
			totalSuccess += success;
			totalFailure += failure;
			totalException += exception;
		}

		resultLog.append("== 평균 결과 ==\n")
			.append("평균 시간: ").append(totalElapsedTime / 10).append("ms\n")
			.append("평균 성공 수: ").append(totalSuccess / 10).append("\n")
			.append("평균 실패 수: ").append(totalFailure / 10).append("\n")
			.append("평균 예외 수: ").append(totalException / 10).append("\n");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("redis_lock_test_summary.txt", true))) {
			writer.write(resultLog.toString());
			writer.newLine();
		}

		System.out.println(resultLog);

		assertThat(totalSuccess / 10).withFailMessage("평균 성공 수는 최대 참가자 수와 같아야 합니다.").isEqualTo(MAX_PARTICIPANTS);
		assertThat(totalFailure / 10).withFailMessage("평균 실패 수는 (총 사용자 수 - 최대 참가자 수)와 같아야 합니다.")
			.isEqualTo(totalUsers - MAX_PARTICIPANTS);
	}
}
