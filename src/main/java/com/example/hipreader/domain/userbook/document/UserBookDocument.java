package com.example.hipreader.domain.userbook.document;

import org.springframework.boot.autoconfigure.graphql.ConditionalOnGraphQlSchema;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.example.hipreader.domain.user.vo.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(indexName = "userbook")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBookDocument {

	@Id
	private String id;

	private Long bookId;

	@Field(type = FieldType.Keyword)
	private String title;

	@Field(type = FieldType.Keyword)
	private String author;

	@Field(type = FieldType.Keyword)
	private String publisher;

	@Field(type = FieldType.Keyword)
	private String coverImage;

	@Field(type = FieldType.Keyword)
	private String categoryName;

	@Field(type = FieldType.Keyword)
	private String gender;

	private Integer ageGroup; // 10, 20, 30, 40
}
