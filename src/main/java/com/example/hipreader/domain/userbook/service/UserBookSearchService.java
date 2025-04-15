package com.example.hipreader.domain.userbook.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import com.example.hipreader.domain.user.gender.Gender;
import com.example.hipreader.domain.userbook.document.UserBookDocument;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBookSearchService {

	private final ElasticsearchOperations elasticsearchOperations;

	public Page<UserBookDocument> searchByCondition(
		Integer ageGroup,
		Gender gender,
		String categoryName,
		Pageable pageable
	) {
		Criteria criteria = new Criteria();

		if (ageGroup != null) {
			criteria = criteria.and(new Criteria("ageGroup").is(ageGroup));
		}
		if (gender != null) {
			criteria = criteria.and(new Criteria("gender").is(gender.name()));
		}
		if (categoryName != null && !categoryName.isBlank()) {
			criteria = criteria.and(new Criteria("categoryName").is(categoryName));
		}

		CriteriaQuery query = new CriteriaQuery(criteria, pageable);
		SearchHits<UserBookDocument> searchHits = elasticsearchOperations.search(query, UserBookDocument.class);

		List<UserBookDocument> content = searchHits.stream()
			.map(SearchHit::getContent)
			.toList();

		return new PageImpl<>(content, pageable, searchHits.getTotalHits());
	}
}

