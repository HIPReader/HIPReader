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

}
