package com.example.hipreader.common.util;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.domain.user.role.UserRole;

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

	private static final String BEARER_PREFIX = "Bearer ";
	private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 1시간
	private static final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60; // 2주

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

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("email", email)
			.claim("role", role)
			.claim("nickname", nickname)
			.setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
			.setIssuedAt(date) //발급일
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	public String createRefreshToken(Long userId) {
		Date now = new Date();

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("type", "refresh")
			.setIssuedAt(now)
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME * 1000))
			.signWith(key, signatureAlgorithm)
			.compact();

	}

	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(7);
		}
		throw new UnauthorizedException(INVALID_TOKEN);
	}

	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public String extractNickname(String token) {
		return extractClaims(token).get("nickname", String.class);
	}

	public boolean validateRefreshToken(String refreshToken) {
		try {
			if (refreshToken.startsWith(BEARER_PREFIX)) {
				refreshToken = refreshToken.substring(BEARER_PREFIX.length());
			}

			Jwts.parserBuilder()
				.setSigningKey(key) // Signing Key 반환
				.build()
				.parseClaimsJws(refreshToken); // Refresh Token 검증
			return true;
		} catch (ExpiredJwtException e) {
			log.error("만료된 RefreshToken 입니다");
			throw new UnauthorizedException(EXPIRED_TOKEN);
		} catch (JwtException e) {
			log.error("검증되지 않은 RefreshToken 입니다");
			throw new UnauthorizedException(INVALID_TOKEN);
		}
	}
}
