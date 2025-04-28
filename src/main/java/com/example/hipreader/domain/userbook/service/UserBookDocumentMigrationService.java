package com.example.hipreader.domain.userbook.service;

import com.example.hipreader.domain.book.entity.Author;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.AuthorRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.userbook.document.UserBookDocument;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBookDocumentMigrationService {

	private final UserBookRepository userBookRepository;
	private final AuthorRepository authorRepository;
	private final ElasticsearchOperations elasticsearchOperations;

	public void migrate() {
		// 1. 기존 인덱스 삭제 후 재생성
		IndexOperations indexOps = elasticsearchOperations.indexOps(UserBookDocument.class);
		if (indexOps.exists()) {
			indexOps.delete();
		}
		indexOps.create();
		indexOps.putMapping();

		// 2. DB 에서 전체 userBook 데이터 조회
		List<UserBook> userBooks = userBookRepository.findAllWithUserAndBook();

		// 3. document 로 변환 후 저장
		List<UserBookDocument> userBookDocuments = userBooks.stream()
			.map(this::convertToDocument)
			.collect(Collectors.toList());

		elasticsearchOperations.save(userBookDocuments);
	}

	private UserBookDocument convertToDocument(UserBook userBook) {
		User user = userBook.getUser();
		Book book = userBook.getBook();
		List<Author> authorList = authorRepository.findAuthorsByBookId(book.getId());

		String authors = authorList.stream()
			.map(Author::getName)
			.collect(Collectors.joining(", "));

		return UserBookDocument.builder()
			.id(user.getId() + "_" + book.getId())
			.bookId(book.getId())
			.title(book.getTitle())
			.author(authors)
			.publisher(book.getPublisher())
			.coverImage(book.getCoverImage())
			.gender(user.getGender().name())
			.categoryName(book.getCategoryName())
			.ageGroup((user.getAge() / 10) * 10)
			.build();
	}
}
