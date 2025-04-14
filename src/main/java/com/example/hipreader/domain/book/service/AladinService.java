package com.example.hipreader.domain.book.service;

import com.example.hipreader.domain.book.dto.request.AladinBookDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinService {

	@Value("${aladin.api.key}")
	private String key;
	private final RestTemplate restTemplate;

	public List<AladinBookDto> searchBooks(String keyword) {
		StringBuilder urlBuilder = new StringBuilder("http://www.aladin.co.kr/ttb/api/ItemSearch.aspx");
		urlBuilder.append("?ttbkey=").append(key)
				.append("&Query=").append(keyword)
				.append("&QueryType=Keyword")
				.append("&MaxResults=10")
				.append("&start=1")
				.append("&SearchTarget=Book")
				.append("&output=js")
				.append("&Version=20131101");

		String url = urlBuilder.toString();

		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());
			JsonNode items = root.get("item");

			if (items == null || !items.isArray()) {
				log.warn("알라딘 API 응답에 'item'이 없음 또는 배열 아님 - keyword: {}", keyword);
				return Collections.emptyList();
			}

			List<AladinBookDto> books = new ArrayList<>();
			for (JsonNode item : items) {
				AladinBookDto dto = new AladinBookDto();
				dto.setTitle(item.get("title").asText());
				dto.setAuthor(item.get("author").asText());
				dto.setIsbn13(item.get("isbn13").asText());
				dto.setPublisher(item.get("publisher").asText());
				dto.setPubDate(item.get("pubDate").asText());
				dto.setCover(item.get("cover").asText());

				JsonNode subInfo = item.get("subInfo");
				Integer itemPage = null;
				if (subInfo != null && subInfo.has("itemPage") && subInfo.get("itemPage").canConvertToInt()) {
					itemPage = subInfo.get("itemPage").asInt();
				}
				dto.setItemPage(itemPage);

				String fullCategoryName = item.has("categoryName") ? item.get("categoryName").asText() : null;
				if (fullCategoryName != null && fullCategoryName.contains(">")) {
					String[] categories = fullCategoryName.split(">");
					if (categories.length > 1) {
						dto.setCategoryName(categories[1].trim());
					}
				}

				books.add(dto);
			}

			return books;
		} catch (Exception e) {
			log.error("알라딘 API 처리 중 예외 발생 - keyword: {}, error: {}", keyword, e.getMessage());
			return Collections.emptyList();
		}
	}


	public Integer fetchItemPageFromItemLookUp(String isbn13) {
		StringBuilder urlbuilder = new StringBuilder("http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx");
		urlbuilder.append("?ttbkey=").append(key)
				.append("&itemIdType=ISBN13")
				.append("&itemId=").append(isbn13)
				.append("&output=js")
				.append("&Version=20131101")
				.append("&optResult=subInfo");

		String url = urlbuilder.toString();

		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());
			JsonNode item = root.get("item");

			if (item != null && item.isArray() && item.size() > 0) {
				JsonNode subInfo = item.get(0).get("subInfo");
				if (subInfo != null && subInfo.has("itemPage") && subInfo.get("itemPage").canConvertToInt()) {
					int page = subInfo.get("itemPage").asInt();
					log.info("ItemLookUp에서 조회된 itemPage: {} (isbn13: {})", page, isbn13);
					return page;
				}
			}
			log.warn("ItemLookUp에서도 itemPage 없음 (isbn13: {})", isbn13);
		} catch (Exception e) {
			log.error("ItemLookUp 호출 실패 (isbn13: {})", isbn13, e);
		}

		return null;
	}
}

