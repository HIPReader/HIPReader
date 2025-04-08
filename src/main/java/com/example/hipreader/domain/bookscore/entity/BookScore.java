package com.example.hipreader.domain.bookscore.entity;

import com.example.hipreader.domain.userbook.status.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_scores")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookScore {

	@Id
	private Long bookId; //Book의 ID와 연결

	@Column(nullable = false)
	private long toRead = 0L;

	@Column(nullable = false)
	private long reading = 0L;

	@Column(nullable = false)
	private long finished = 0L;

	@Transient
	public long getTotalScore() {
		return toRead + reading * 2 + finished * 3;
	}

	public void updateCount(Status oldStatus, Status newStatus) {
		if (oldStatus != null) decrement(oldStatus);
		increment(newStatus);
	}

	public void decrement(Status status) {
		switch (status) {
			case TO_READ -> toRead--;
			case READING -> reading--;
			case FINISHED -> finished--;
		}
	}

	public void increment(Status status) {
		switch (status) {
			case TO_READ -> toRead++;
			case READING -> reading++;
			case FINISHED -> finished++;
		}
	}

}
