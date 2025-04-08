package com.example.hipreader.domain.refreshtoken.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.domain.refreshtoken.entity.RefreshToken;
import com.example.hipreader.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public String refreshAccessToken(String refreshToken) {
		log.debug("Refresh token requested for token: {}", refreshToken);
		RefreshToken findRefreshToken = findValidRefreshToken(refreshToken);
		User user = findUserByToken(findRefreshToken);
		return createNewAccessToken(user);
	}

	// 검증 로직 분리
	private RefreshToken findValidRefreshToken(String token) {
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
			.orElseThrow(() -> new NotFoundException(TOKEN_NOT_FOUND));
		if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
			throw new NotFoundException(INVALID_TOKEN);
		}
		return refreshToken;
	}

	// 사용자 조회 로직 분리
	private User findUserByToken(RefreshToken token) {
		return userRepository.findById(token.getUserId())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}

	// 토큰 생성 로직 분리
	private String createNewAccessToken(User user) {
		return jwtUtil.createAccessToken(
			user.getId(),
			user.getEmail(),
			user.getRole(),
			user.getNickname()
		);
	}
}
