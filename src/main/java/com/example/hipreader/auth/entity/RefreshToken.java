package com.example.hipreader.auth.entity;

import static com.example.hipreader.common.util.JwtUtil.*;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import org.springframework.data.annotation.Id;

import com.example.hipreader.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@RedisHash(value = "refreshToken", timeToLive = 14 * 24 * 60 * 60) // 7일
public class RefreshToken {

	@Id
	private String refreshToken;

	@Indexed
	private Long userId;

	public RefreshToken(String refreshToken, Long userId) {
		this.refreshToken = refreshToken;
		this.userId = userId;
	}

	public static RefreshToken create(String refreshToken, User user) {
		return new RefreshToken(refreshToken, user.getId());
	}
}
