package com.example.hipreader.common.util;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.role.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
	private final UserRepository userRepository;
	private final RedisTemplate<String,String> redisTemplate;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");
		log.info("Authorization Header: {}", authorizationHeader);

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = jwtUtil.substringToken(authorizationHeader);
			try {
				Claims claims = jwtUtil.extractClaims(token);
				setAuthentication(claims); // 인증 설정 로직 분리
				log.info("추출한 JWT: {}", token);

			} catch (ExpiredJwtException e) {
				log.error("만료된 JWT token 입니다.", e);
				handleExpiredToken(response,token);
				throw new ResponseStatusException(EXPIRED_TOKEN.getStatus(), EXPIRED_TOKEN.getMessage());
			} catch (JwtException e) {
				log.error("유효하지 않는 Access Token 입니다.", e);
				throw new ResponseStatusException(INVALID_ACCESS_TOKEN.getStatus(),INVALID_ACCESS_TOKEN.getMessage());
			} catch (Exception e) {
				log.error("Internal server error", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		filterChain.doFilter(request, response);
	}

	private void handleExpiredToken(HttpServletResponse response, String expiredToken) throws IOException {
		try {
			String refreshToken = redisTemplate.opsForValue().get("RT:" + expiredToken);
			if (refreshToken != null) {
				String pureToken = refreshToken.replaceAll("Bearer ", "");
				jwtUtil.validateRefreshToken(pureToken);
				redisTemplate.delete("RT:" + expiredToken);

				Long userId = Long.valueOf(jwtUtil.extractClaims(pureToken).getId());
				User user = userRepository.findById(userId)
					.orElseThrow(
						() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

				String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole(),
					user.getNickname());
				String newRefreshToken = jwtUtil.createRefreshToken(user.getId());

				redisTemplate.opsForValue().set("RT:" + newAccessToken, newRefreshToken, 7, TimeUnit.DAYS);

				response.setHeader("New-Access-Token", newAccessToken);
				response.setHeader("New-Refresh-Token", newRefreshToken);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				throw new ResponseStatusException(NEED_LOGIN.getStatus(), NEED_LOGIN.getMessage());
			}
		} catch (Exception e) {
			log.error("토큰 재발급 실패", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void setAuthentication(Claims claims) {
		Long userId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		UserRole userRole = UserRole.of(claims.get("role", String.class));

		AuthUser authUser = new AuthUser(userId, email, userRole);
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);

		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}
}
