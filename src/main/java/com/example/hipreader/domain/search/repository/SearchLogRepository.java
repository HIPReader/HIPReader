package com.example.hipreader.domain.search.repository;

import com.example.hipreader.domain.search.entity.SearchLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchLogRepository extends ElasticsearchRepository<SearchLog, Long> {

}
