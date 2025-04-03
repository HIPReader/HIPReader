package com.example.hipreader.domain.userbook.entity;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.userbook.status.Status;
import com.example.hipreader.domain.user.entity.User;
import jakarta.persistence.*;
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
public class UserBook {

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
                throw new IllegalStateException("TO_READ 상태에서는 READING으로만 변경할 수 있습니다.");
            if (progress != 0)
                throw new IllegalArgumentException("TO_READ 상태에서는 progress를 0으로 유지해야 합니다.");
        }

        if (this.status == Status.READING && newStatus == Status.FINISHED) {
            if (progress != book.getTotalPages())
                throw new IllegalArgumentException("FINISHED 상태로 변경하려면 전체 페이지를 읽어야 합니다.");
        }

        if (progress < 0 || progress > book.getTotalPages()) {
            throw new IllegalArgumentException("progress는 0부터 전체 페이지 수 사이여야 합니다.");
        }

        this.status = newStatus;
        this.progress = progress;
    }

}
