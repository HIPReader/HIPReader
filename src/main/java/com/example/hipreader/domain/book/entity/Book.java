package com.example.hipreader.domain.book.entity;

import com.example.hipreader.common.entity.TimeStamped;
import com.example.hipreader.domain.book.genre.Genre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book extends TimeStamped {
    //제목,작가,출판사,장르,총페이지수,이미지
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn13;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private LocalDate datePublished;

    private Integer totalPages;

    @Column
    private String coverImage;

    @Column
    @Enumerated(EnumType.STRING)
    private Genre genre;

    public void updateBook(Genre genre, String title,String author,
                           String publisher, LocalDate datePublished, Integer totalPages, String coverImage) {

        this.genre = genre != null ? genre : this.genre;
        this.title = title != null ? title : this.title;
        this.author = author != null ? author : this.author;
        this.publisher = publisher != null ? publisher : this.publisher;
        this.datePublished = datePublished != null ? datePublished : this.datePublished;
        this.totalPages = totalPages != null ? totalPages : this.totalPages;
        this.coverImage = coverImage != null ? coverImage : this.coverImage;
    }
}

