package com.example.hipreader.domain.refreshtoken.entity;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RedisHash(value = "token", timeToLive = 604800) // 7일
@NoArgsConstructor
@Getter
public class RefreshToken {

	@Id
	private Long userId;

	@Indexed
	private String refreshToken;

	public RefreshToken(Long userId, String refreshToken) {
		this.userId = userId;
		this.refreshToken = refreshToken;
	}
}
