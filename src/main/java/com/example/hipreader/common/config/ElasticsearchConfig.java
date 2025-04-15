package com.example.hipreader.common.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
			.connectedTo("localhost:9200")
			.build();
	}

	@Bean
	public RestClient restClient() {
		return RestClient.builder(
				new HttpHost("localhost", 9200, "http"))
			.build();
	}

	@Bean
	public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
		return new RestClientTransport(restClient, new JacksonJsonpMapper());
	}

	@Bean
	public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
		return new ElasticsearchClient(transport);
	}

	@Bean
	public ElasticsearchOperations elasticsearchOperations(ElasticsearchTransport transport) {
		return new ElasticsearchTemplate(elasticsearchClient(transport));
	}
}
