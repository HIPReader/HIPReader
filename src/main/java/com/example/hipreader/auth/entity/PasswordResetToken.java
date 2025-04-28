package com.example.hipreader.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String token; // UUID

	@Column(nullable = false)
	private LocalDateTime expiryDate;

	@Column(nullable = false)
	private String email; // User.email과 연결

	@Builder
	public PasswordResetToken(String email, String token) {
		this.email = email;
		this.token = token;
		this.expiryDate = LocalDateTime.now().plusHours(1);
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiryDate);
	}
}
