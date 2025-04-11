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

	public Integer fetchItemPageFromItemLookUp(String isbn13) {
		String url = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx"
			+ "?ttbkey=" + key
			+ "&itemIdType=ISBN13"
			+ "&ItemId=" + isbn13
			+ "&output=js"
			+ "&Version=20131101"
			+ "&optResult=subInfo";

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

	public List<AladinBookDto> searchBooks(String keyword) {
		String url = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx"
			+ "?ttbkey=" + key
			+ "&Query=" + keyword
			+ "&QueryType=Title"
			+ "&MaxResults=10"
			+ "&start=1"
			+ "&SearchTarget=Book"
			+ "&output=js"
			+ "&Version=20131101";

		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode root = mapper.readTree(response.getBody());
			JsonNode items = root.get("item");

			if (items == null || !items.isArray()) {
				log.warn("알라딘 API 응답에 'item'이 없음 or 배열 아님 - keyword: {}", keyword);
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

				books.add(dto);
			}

			return books;
		} catch (Exception e) {
			log.error("알라딘 API 처리 중 예외 발생 - keyword: {}, error: {}", keyword, e.getMessage());
			return Collections.emptyList();
		}
	}
}

