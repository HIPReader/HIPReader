package com.example.heapreader.refreshtoken.entity;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@RedisHash(value = "token", timeToLive = 604800) // 7일
@NoArgsConstructor
@Getter
@ToString
public class RefreshToken {

	@Id
	private Long id;

	@Indexed
	private String token;

	public RefreshToken(Long id, String token) {
		this.id = id;
		this.token = token;
	}
}
