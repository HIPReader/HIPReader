package com.example.hipreader.domain.userbook.repository;

import static com.example.hipreader.domain.book.entity.QBook.*;
import static com.example.hipreader.domain.user.entity.QUser.*;
import static com.example.hipreader.domain.userbook.entity.QUserBook.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.genre.Genre;
import com.example.hipreader.domain.user.gender.Gender;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserBookRepositoryImpl implements UserBookRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Book> findRecommendedBooks(Integer age, Gender gender, String categoryName, Pageable pageable) {

		BooleanBuilder builder = new BooleanBuilder();

		// 나이 필터링
		if (age != null) {
			int ageStart = (age / 10) * 10;
			int ageEnd = ageStart + 9;
			builder.and(user.age.between(ageStart, ageEnd));
		}

		// 성별 필터링
		if (gender != null) {
			builder.and(user.gender.eq(gender));
		}

		// 카테고리 필터링
		if (categoryName != null) {
			builder.and(book.categoryName.eq(categoryName));
		}

		// 컨텐츠 조회
		List<Book> content = queryFactory
			.select(userBook.book)
			.from(userBook)
			.join(userBook.user, user)
			.join(userBook.book, book)
			.where(builder)
			.groupBy(userBook.book.id)
			.orderBy(userBook.book.count().desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 개수 조회 ( distinct count )
		Long total = queryFactory
			.select(userBook.book.id.countDistinct())
			.from(userBook)
			.join(userBook.user, user)
			.join(userBook.book, book)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total);
	}
}
