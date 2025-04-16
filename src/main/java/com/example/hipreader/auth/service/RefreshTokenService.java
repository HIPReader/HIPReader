package com.example.hipreader.auth.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.auth.entity.RefreshToken;
import com.example.hipreader.auth.repository.RefreshTokenRepository;
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

	@Transactional
	public String refreshAccessToken(String refreshToken) {
		// 1. 리프레시 토큰 유효성 검증 (서명, 만료일자)
		if (!jwtUtil.validateRefreshToken(refreshToken)) {
			throw new UnauthorizedException(INVALID_REFRESH_TOKEN);
		}

		// 2. Redis에서 리프레시 토큰 존재 여부 확인
		RefreshToken storedToken = refreshTokenRepository.findById(refreshToken)
			.orElseThrow(() -> new UnauthorizedException(INVALID_REFRESH_TOKEN));

		// 3. 토큰에서 userId 추출 및 사용자 조회
		Long userId = storedToken.getUserId();
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		// 4. 기존 리프레시 토큰 삭제 (옵션: 새로운 토큰 발급 시)
		refreshTokenRepository.deleteById(refreshToken);

		// 5. 새로운 액세스 토큰 발급
		return jwtUtil.createAccessToken(
			user.getId(),
			user.getEmail(),
			user.getRole(),
			user.getNickname()
		);
	}
}
