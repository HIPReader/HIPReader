package com.example.hipreader.auth.entity;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@RedisHash(value = "refreshToken") // 7일
public class RefreshToken {

	@Id
	private String id;

	@Indexed
	private String refreshToken;

	private Long userId;

	public RefreshToken(Long userId, String refreshToken) {
		this.userId = userId;
		this.refreshToken = refreshToken;
	}
}
