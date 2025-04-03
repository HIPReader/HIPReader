package com.example.hipreader.common.util;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

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

	public String createRefreshToken(Long userId) {
		Date now = new Date();

		return BEARER_PREFIX + Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setIssuedAt(now)
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
			.signWith(key, signatureAlgorithm)
			.compact();

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
				.setSigningKey(key) // Signing Key 반환
				.build()
				.parseClaimsJws(refreshToken); // Refresh Token 검증
			return true;
		} catch (ExpiredJwtException e) {
			log.error("만료된 RefreshToken 입니다");
			throw new ResponseStatusException(EXPIRED_TOKEN.getStatus(),EXPIRED_TOKEN.getMessage());
		} catch (JwtException e) {
			log.error("검증되지 않은 RefreshToken 입니다");
			throw new ResponseStatusException(INVALID_TOKEN.getStatus(),INVALID_TOKEN.getMessage());
		}
	}
}
