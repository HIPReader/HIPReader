package com.example.hipreader.common.util;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.refreshtoken.entity.RefreshToken;
import com.example.hipreader.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.role.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain

	) throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");
		log.info("Authorization Header: {}", authorizationHeader);

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwt = jwtUtil.substringToken(authorizationHeader);
			try {
				Claims claims = jwtUtil.extractClaims(jwt);
				log.info("Extracted JWT: {}", jwt);

				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					setAuthentication(claims);
				}
			} catch (ExpiredJwtException e) {
				log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
				handleExpiredToken(response, jwt);
			} catch (SecurityException | MalformedJwtException e) {
				log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
			} catch (UnsupportedJwtException e) {
				log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
			} catch (Exception e) {
				log.error("Internal server error", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		filterChain.doFilter(request, response);
	}

	private void handleExpiredToken(HttpServletResponse response, String expiredToken) throws IOException {
		RefreshToken refreshToken = getRefreshToken(expiredToken);
		if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
			String newAccessToken = reCreateAccessToken(refreshToken); // AuthService 메서드 대신 내부 메서드 사용
			response.setHeader("New-Access-Token", newAccessToken);
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "RefreshToken이 만료되었습니다. 재로그인이 필요합니다.");
		}
	}

	private void setAuthentication(Claims claims) {
		Long userId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		UserRole userRole = UserRole.of(claims.get("userRole", String.class));

		AuthUser authUser = new AuthUser(userId, email, userRole);
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	public RefreshToken getRefreshToken(String accessToken) {
		return refreshTokenRepository.findByToken(accessToken).orElse(null);
	}

	public String reCreateAccessToken(RefreshToken refreshToken) {
		Long userId = refreshToken.getUserId();
		User user = userRepository.findById(userId).orElseThrow(
			() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
		String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getUserRole(),user.getNickname());

		refreshTokenRepository.save(new RefreshToken(userId, newAccessToken));
		return newAccessToken;
	}
}
