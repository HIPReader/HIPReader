package com.example.hipreader.domain.userbook.entity;

import com.example.hipreader.common.entity.TimeStamped;
import static com.example.hipreader.common.exception.ErrorCode.*;

import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.userbook.status.Status;
import com.example.hipreader.domain.user.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_books",
	uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
public class UserBook extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	private int progress;

	@Enumerated(EnumType.STRING)
	private Status status;

	public void update(Status newStatus, int progress) {
		if (this.status == Status.TO_READ) {
			if (newStatus != Status.READING)
				throw new BadRequestException(INVALID_STATUS_TRANSITION);
			if (progress == 0)
				throw new BadRequestException(INVALID_PROGRESS_FOR_TO_READ);
		}

		// 페이지 수 유효성 체크 : 이미 register에서 page가 있는 책만 등록할 수 있게 했으므로,
		// progress는 0보다 크고 전체 페이지보다 작으며 현재 읽은 페이지보다 커야 한다.
		if (progress < 0 || progress > book.getTotalPages() || this.progress >= progress) {
			throw new BadRequestException(INVALID_PROGRESS_RANGE);
		}

		// percentage가 100%면 자동으로 FINISHED로 전환
		if (book.getTotalPages() > 0 && progress == book.getTotalPages()) {
			this.status = Status.FINISHED;
		} else {
			this.status = newStatus;
		}

		this.progress = progress;
	}

}
