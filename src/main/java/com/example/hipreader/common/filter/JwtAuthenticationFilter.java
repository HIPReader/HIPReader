package com.example.hipreader.common.filter;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.config.JwtAuthenticationToken;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.common.util.JwtUtil;
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

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = jwtUtil.substringToken(authorizationHeader);
			try {
				Claims claims = jwtUtil.extractClaims(token);
				setAuthentication(claims);

			} catch (ExpiredJwtException e) {
				log.error("만료된 JWT token 입니다.", e);
				handleExpiredToken(response,token);
				throw new UnauthorizedException(EXPIRED_TOKEN);
			} catch (JwtException e) {
				log.error("유효하지 않는 Access Token 입니다.", e);
				throw new UnauthorizedException(INVALID_ACCESS_TOKEN);
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
				jwtUtil.validateRefreshToken(refreshToken);
				redisTemplate.delete("RT:" + expiredToken);

				Long userId = Long.valueOf(jwtUtil.extractClaims(refreshToken).getId());
				User user = userRepository.findById(userId)
					.orElseThrow(
						() -> new NotFoundException(USER_NOT_FOUND));

				String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole(),
					user.getNickname());
				String newRefreshToken = jwtUtil.createRefreshToken(user.getId());

				redisTemplate.opsForValue().set("RT:" + newAccessToken, newRefreshToken, 7, TimeUnit.DAYS);

				response.setHeader("New-Access-Token", newAccessToken);
				response.setHeader("New-Refresh-Token", newRefreshToken);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				throw new UnauthorizedException(NEED_LOGIN);
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
