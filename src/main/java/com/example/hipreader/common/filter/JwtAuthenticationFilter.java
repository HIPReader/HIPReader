package com.example.hipreader.common.filter;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.config.JwtAuthenticationToken;
import com.example.hipreader.common.exception.ErrorCode;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.domain.user.vo.UserRole;

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

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		try {
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				String token = jwtUtil.substringToken(authorizationHeader);
				Claims claims = jwtUtil.extractClaims(token);
				setAuthentication(claims);
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT token 입니다.", e);
			handleException(response, NEED_LOGIN);
		} catch (JwtException | IllegalArgumentException e) {
			log.error("유효하지 않은 Access Token 입니다.", e);
			handleException(response, INVALID_ACCESS_TOKEN);
		} catch (Exception e) {
			log.error("Internal server error", e);
			handleException(response, INTERNAL_SERVER_ERROR);
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

	private void handleException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		SecurityContextHolder.clearContext(); // SecurityContext 초기화
		response.sendError(errorCode.getStatus().value(), errorCode.getMessage());
	}
}
