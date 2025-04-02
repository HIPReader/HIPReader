package com.example.heapreader.common.util;

import static com.example.heapreader.common.exception.ErrorCode.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.heapreader.domain.refreshtoken.entity.RefreshToken;
import com.example.heapreader.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.heapreader.domain.user.role.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisTemplate<String, String> redisTemplate;

	private static final String BEARER_PREFIX = "Bearer ";
	private static final long ACCESS_TOKEN_TIME = 60 * 60 * 7 *24 *1000L;  //1주일(테스트중이라 1주일로 바꿈)
	private static final long REFRESH_TOKEN_TIME = 60 * 60 * 7 *24 *1000L; //1주일

	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String createAccessToken(Long userId, String email, UserRole role, String nickname) {
		Date date = new Date();

		return BEARER_PREFIX + Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("email", email)
			.claim("role", role)
			.claim("nickname", nickname)
			.setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
			.setIssuedAt(date) //발급일
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	// Refresh Token 생성 로직 분리
	private String generateRefreshTokenValue(Long userId) {
		return Jwts.builder()
			.setClaims(new HashMap<>() {{ put("sub", userId); }})
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
			.signWith(key, SignatureAlgorithm.HS512) // 알고리즘 강화
			.compact();
	}

	// 저장 전 중복 토큰 체크 추가
	public String createRefreshToken(Long userId) {

		String refreshToken = generateRefreshTokenValue(userId);

		refreshTokenRepository.findByToken(refreshToken)
			.ifPresent(t -> { throw new ResponseStatusException(TOKEN_DUPLICATED.getStatus(), TOKEN_DUPLICATED.getMessage());
			});
		// Redis 저장 (Repository 사용)
		refreshTokenRepository.save(new RefreshToken(userId, refreshToken));

		return refreshToken;
	}

	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(7);
		}
		throw new ResponseStatusException(INVALID_TOKEN.getStatus(),INVALID_TOKEN.getMessage());
	}

	public Claims extractClaims(String token) {
		System.out.println(token);
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public boolean validateRefreshToken(String refreshToken) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(getSigningKey()) // Signing Key 반환
				.build()
				.parseClaimsJws(refreshToken); // Refresh Token 검증
			return true;
		} catch (ExpiredJwtException e) {
			log.error("만료된 RefreshToken 입니다");
			throw new ResponseStatusException(INVALID_TOKEN.getStatus(),INVALID_TOKEN.getMessage());
		} catch (JwtException e) {
			log.error("검증되지 않은 RefreshToken 입니다");
			throw new ResponseStatusException(INVALID_TOKEN.getStatus(),INVALID_TOKEN.getMessage());
		}
	}

	private Key getSigningKey() {
		return key; // @PostConstruct로 초기화된 key를 반환
	}
}
