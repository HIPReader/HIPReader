package com.example.hipreader.auth.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.auth.dto.request.ForgotPasswordRequestDto;
import com.example.hipreader.auth.dto.request.ResetPasswordRequestDto;
import com.example.hipreader.auth.dto.request.SigninRequestDto;
import com.example.hipreader.auth.dto.request.SignupRequestDto;
import com.example.hipreader.auth.dto.response.SigninResponseDto;
import com.example.hipreader.auth.dto.response.SignupResponseDto;
import com.example.hipreader.auth.entity.PasswordResetToken;
import com.example.hipreader.auth.repository.PasswordResetTokenRepository;
import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.ConflictException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.auth.entity.RefreshToken;
import com.example.hipreader.auth.repository.RefreshTokenRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.UserRole;
import com.example.hipreader.domain.userdiscussion.service.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final EmailService emailService;

	@Transactional
	public SignupResponseDto signUp(
		@Valid SignupRequestDto signupRequestDto) {

		if (userRepository.existsUserByEmail(signupRequestDto.getEmail())) {
			throw new ConflictException(USER_EMAIL_DUPLICATION);
		}

		String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

		UserRole userRole = UserRole.of(signupRequestDto.getRole());
		Gender gender = Gender.valueOf(signupRequestDto.getGender());

		User newUser = User.builder()
			.nickname(signupRequestDto.getNickname())
			.email(signupRequestDto.getEmail())
			.password(encodedPassword)
			.role(userRole)
			.age(signupRequestDto.getAge())
			.gender(gender)
			.build();

		User savedUser = userRepository.save(newUser);

		String accessToken = jwtUtil.createAccessToken(savedUser.getId(), savedUser.getEmail(), userRole,
			savedUser.getNickname());

		return new SignupResponseDto(accessToken);
	}

	@Transactional
	public SigninResponseDto signIn(@Valid SigninRequestDto signinRequestDto) {
		User user = userRepository.findByEmail(signinRequestDto.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		if (!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
			throw new BadRequestException(INVALID_PASSWORD);
		}

		// 기존 리프레시 토큰 삭제 (userId 기반)
		refreshTokenRepository.deleteByUserId(user.getId());

		// 새 토큰 생성
		String accessToken = jwtUtil.createAccessToken(
			user.getId(),
			user.getEmail(),
			user.getRole(),
			user.getNickname()
		);
		String refreshToken = jwtUtil.createRefreshToken(user.getId());

		refreshTokenRepository.save(new RefreshToken(refreshToken, user.getId()));

		return new SigninResponseDto(accessToken, refreshToken, user.getNickname());
	}

	@Transactional
	public void requestPasswordReset(ForgotPasswordRequestDto forgotPasswordRequestDto) {
		User user = userRepository.findByEmail(forgotPasswordRequestDto.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		passwordResetTokenRepository.deleteByEmail(user.getEmail()); // 기존 토큰 삭제

		String token = UUID.randomUUID().toString();
		passwordResetTokenRepository.save(new PasswordResetToken(user.getEmail(), token));

		emailService.sendPasswordResetEmail(user.getEmail(), token);//이메일 발송은 외부 트랜잭션에서 하는게 좋다..
	}

	@Transactional
	public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
		PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(resetPasswordRequestDto.getToken())
			.orElseThrow(() -> new NotFoundException(TOKEN_NOT_FOUND));

		if (resetToken.isExpired()) {
			throw new UnauthorizedException(EXPIRED_TOKEN);
		}

		User user = userRepository.findByEmail(resetToken.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		user.changePassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword())); // 중요: 암호화 필수
		userRepository.save(user);
		passwordResetTokenRepository.delete(resetToken);
	}
}
