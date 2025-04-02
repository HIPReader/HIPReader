package com.example.heapreader.domain.refreshtoken.service;

import static com.example.heapreader.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.heapreader.common.util.JwtUtil;
import com.example.heapreader.domain.refreshtoken.entity.RefreshToken;
import com.example.heapreader.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.heapreader.domain.user.entity.User;
import com.example.heapreader.domain.user.repository.UserRepository;
import com.example.heapreader.domain.user.service.UserService;

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
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
			.orElseThrow(() -> new ResponseStatusException(TOKEN_NOT_FOUND.getStatus(),TOKEN_NOT_FOUND.getMessage()));
		if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
			throw new ResponseStatusException(INVALID_TOKEN.getStatus(),INVALID_TOKEN.getMessage());
		}
		return refreshToken;
	}

	// 사용자 조회 로직 분리
	private User findUserByToken(RefreshToken token) {
		return userRepository.findById(token.getUserId())
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(),USER_NOT_FOUND.getMessage()));
	}

	// 토큰 생성 로직 분리
	private String createNewAccessToken(User user) {
		return jwtUtil.createAccessToken(
			user.getId(),
			user.getEmail(),
			user.getUserRole(),
			user.getNickname()
		);
	}
}
